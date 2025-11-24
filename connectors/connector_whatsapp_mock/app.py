from flask import Flask, request, jsonify
from confluent_kafka import Consumer, Producer
import threading
import json
import time

app = Flask(__name__)

KAFKA_BROKER = "localhost:29092"
TOPIC_IN = "msg_whatsapp_outbound"
TOPIC_OUT = "status_updates"

producer = Producer({"bootstrap.servers": KAFKA_BROKER})

def delivery_report(err, msg):
    if err is not None:
        print(f"❌ Erro ao enviar callback: {err}")
    else:
        print(f"✅ Callback enviado para {msg.topic()} [{msg.partition()}]")

def consume_messages():
    consumer = Consumer({
        "bootstrap.servers": KAFKA_BROKER,
        "group.id": "whatsapp-mock",
        "auto.offset.reset": "earliest"
    })
    consumer.subscribe([TOPIC_IN])

    while True:
        msg = consumer.poll(1.0)
        if msg is None:
            continue
        if msg.error():
            print(f"Erro no consumidor: {msg.error()}")
            continue

        payload = json.loads(msg.value().decode("utf-8"))
        user_id = payload.get("userId")
        text = payload.get("text")

        print(f"[Instagram] Mensagem recebida para usuário {user_id}: \"{text}\"")

        # Simula atraso de 2 segundos para a entrega
        time.sleep(2) 
        delivered_callback = {
            # CORRIGIDO o nome do canal
            "channel": "whatsapp",
            "userId": user_id,
            "status": "delivered"
        }
        producer.produce(TOPIC_OUT, json.dumps(delivered_callback).encode("utf-8"), callback=delivery_report)
        producer.flush() 
        print(f"   -> Status atualizado para DELIVERED (Entregue)")

        # Simula atraso de 3 segundos para a leitura
        time.sleep(3) 
        read_callback = {
            "channel": "instagram",
            "userId": user_id,
            "status": "read"
        }
        producer.produce(TOPIC_OUT, json.dumps(read_callback).encode("utf-8"), callback=delivery_report)
        producer.flush()
        print(f"   -> Status atualizado para READ (Lido)")

@app.route("/simulate/whatsapp", methods=["POST"])
def simulate_whatsapp():
    data = request.json
    producer.produce(TOPIC_IN, json.dumps(data).encode("utf-8"), callback=delivery_report)
    return jsonify({"ok": True, "message": "Mensagem simulada enviada para WhatsApp"})

if __name__ == "__main__":
    # Thread para consumir mensagens
    t = threading.Thread(target=consume_messages, daemon=True)
    t.start()
    app.run(port=3001)
