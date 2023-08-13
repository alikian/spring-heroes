package com.alikian.cdk;

import org.jetbrains.annotations.NotNull;
import software.amazon.awscdk.CfnParameter;
import software.amazon.awscdk.services.certificatemanager.Certificate;
import software.amazon.awscdk.services.certificatemanager.CertificateProps;
import software.amazon.awscdk.services.certificatemanager.CertificateValidation;
import software.amazon.awscdk.services.dynamodb.ITable;
import software.amazon.awscdk.services.dynamodb.Table;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.ec2.VpcProps;
import software.amazon.awscdk.services.ecs.*;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateService;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedTaskImageOptions;
import software.amazon.awscdk.services.elasticloadbalancingv2.HealthCheck;
import software.amazon.awscdk.services.iam.IGrantable;
import software.amazon.awscdk.services.iot.CfnTopicRule;
import software.amazon.awscdk.services.route53.HostedZone;
import software.amazon.awscdk.services.route53.HostedZoneProviderProps;
import software.amazon.awscdk.services.route53.IHostedZone;
import software.amazon.awscdk.services.secretsmanager.ISecret;
import software.amazon.awscdk.services.secretsmanager.Secret;
import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
// import software.amazon.awscdk.Duration;
// import software.amazon.awscdk.services.sqs.Queue;

public class HeroProjectStack extends Stack {
    public HeroProjectStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public HeroProjectStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        CfnParameter dockerImageName = new CfnParameter(this, "dockerImageName");
        dockerImageName.setDescription("The name of dockerImage");

        CfnParameter secretManagerName = new CfnParameter(this, "secretManagerName");
        secretManagerName.setDescription("The name of secretManagerName");

        CfnParameter dynamoDBName = new CfnParameter(this, "dynamoDBName");
        secretManagerName.setDescription("The name of dynamoDBName");

        ITable dynamoDB=Table.fromTableName(this,"Table",dynamoDBName.getValueAsString());

        ISecret mySecretFromName =
                Secret.fromSecretNameV2(this, "SecretFromName", secretManagerName.getValueAsString());

        IHostedZone iHostedZone = HostedZone.fromLookup(this, "Zone",
                HostedZoneProviderProps.builder().domainName("alikian.me").build());

        Certificate certificate = new Certificate(this, "Certificate",
                CertificateProps.builder()
                        .domainName("heroes.alikian.me")
                        .certificateName("Hero Certificate")
                        .validation(CertificateValidation.fromDns(iHostedZone))
                        .build());

        Vpc vpc = new Vpc(this, "HeroesVpc", VpcProps.builder().maxAzs(2).build());

        Cluster cluster= new Cluster(this,"HeroesCluster",
                ClusterProps.builder().vpc(vpc).build());

        ApplicationLoadBalancedTaskImageOptions options = ApplicationLoadBalancedTaskImageOptions.builder()
                .image(ContainerImage.fromRegistry(dockerImageName.getValueAsString()))
                .containerPort(8080)
                .build();
        ApplicationLoadBalancedFargateService service= ApplicationLoadBalancedFargateService.Builder
                .create(this,"HeroesService")
                .cluster(cluster)
                .cpu(512)
                .desiredCount(1)
                .domainName("heroes.alikian.me")
                .domainZone(iHostedZone)
                .taskImageOptions(options)
                .redirectHttp(true)
                .certificate(certificate)
                .memoryLimitMiB(2048)
                .publicLoadBalancer(true)
                .build();

        mySecretFromName.grantRead(service.getTaskDefinition().getTaskRole());
        dynamoDB.grantReadWriteData(service.getTaskDefinition().getTaskRole());
        service.getTargetGroup().configureHealthCheck(HealthCheck.builder().path("/actuator/health").build());

    }
}
