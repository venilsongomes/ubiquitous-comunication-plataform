import grpc
from message_pb2 import OutboundMessage
from message_pb2_grpc import MessagingServiceStub

channel = grpc.insecure_channel("localhost:50051")
stub = MessagingServiceStub(channel)

resp = stub.SendMessage(
    OutboundMessage(
        channel="instagram",
        userId="user123",
        text="Olá! Teste via gRPC"
    )
)

print("ACK:", resp)
