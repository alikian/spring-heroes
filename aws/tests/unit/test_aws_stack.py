import aws_cdk as core
import aws_cdk.assertions as assertions

from ecs.ecs_stack import AwsStack

# example tests. To run these tests, uncomment this file along with the example
# resource in aws/ecs_stack.py
def test_sqs_queue_created():
    app = core.App()
    stack = AwsStack(app, "aws")
    template = assertions.Template.from_stack(stack)

#     template.has_resource_properties("AWS::SQS::Queue", {
#         "VisibilityTimeout": 300
#     })
