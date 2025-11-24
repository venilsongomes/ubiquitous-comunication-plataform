import grpc
from concurrent import futures
import message_pb2 as message_pb2
import message_pb2_grpc as message_pb2_grpc
from confluent_kafka import Producer
import json

KAFKA_BROKER = "localhost:29092"

producer = Producer({"bootstrap.servers": KAFKA_BROKER})

class MessagingService(message_pb2_grpc.MessagingServiceServicer):

    def SendMessage(self, request, context):
        msg = {
            "channel": request.channel,
            "userId": request.userId,
            "text": request.text
        }

        topic = f"msg_{request.channel}_outbound"

        print(f"[gRPC] → Worker via Kafka: {msg}")

        producer.produce(topic, json.dumps(msg).encode("utf-8"))
        producer.flush()

        return message_pb2.MessageAck(result="ok")

    def ReceiveCallback(self, request, context):
        print(f"[gRPC] CALLBACK recebido: {request}")
        return message_pb2.CallbackAck(result="ack")


def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    message_pb2_grpc.add_MessagingServiceServicer_to_server(MessagingService(), server)
    
    server.add_insecure_port('[::]:50051')
    print("GRPC Server rodando na porta 50051...")
    server.start()
    server.wait_for_termination()

if __name__ == "__main__":
    serve()
