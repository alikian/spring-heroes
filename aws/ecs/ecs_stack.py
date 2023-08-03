from aws_cdk import (aws_ecs as ecs,
                     aws_route53 as route53,
                     aws_certificatemanager as acm,
                     aws_ec2 as ec2,
                     Environment,
                     aws_ecs_patterns as ecs_patterns,
                     Stack, CfnParameter)
from constructs import Construct


class EcsStack(Stack):

    def __init__(self, scope: Construct, construct_id: str, **kwargs) -> None:
        super().__init__(scope, construct_id, **kwargs)

        docker_image_name = CfnParameter(self, "dockerImageName", type="String",
                                  description="The name of dockerImage")

        domain_zone = route53.HostedZone.from_lookup(self, 'Zone', domain_name="alikian.me")
        heroes_acm = acm.Certificate(self, "Certificate",
                                     domain_name="heroes.alikian.me",
                                     certificate_name="Heroes API Service",  # Optionally provide an certificate name
                                     validation=acm.CertificateValidation.from_dns(domain_zone)
                                     )

        vpc = ec2.Vpc(self, "HeroesVpc", max_azs=3)  # default is all AZs in region

        cluster = ecs.Cluster(self, "HeroesCluster", vpc=vpc)

        image_options = ecs_patterns.ApplicationLoadBalancedTaskImageOptions(
            image=ecs.ContainerImage.from_registry(docker_image_name.value_as_string),
            container_port=8080
        )

        service = ecs_patterns.ApplicationLoadBalancedFargateService(self, "HeroesService",
                                                                     cluster=cluster,  # Required
                                                                     cpu=512,  # Default is 256
                                                                     desired_count=1,  # Default is 1
                                                                     domain_name="heroes.alikian.me",
                                                                     domain_zone=domain_zone,
                                                                     task_image_options=image_options,
                                                                     redirect_http=True,
                                                                     certificate=heroes_acm,
                                                                     memory_limit_mib=2048,  # Default is 512
                                                                     public_load_balancer=True)  # Default is True
        service.target_group.configure_health_check(path="/actuator/health", port="8080")
