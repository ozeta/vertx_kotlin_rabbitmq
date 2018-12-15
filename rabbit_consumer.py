import pika

def callback(ch, method, properties, body):
    print(" [x] Received %r" % body)

connection = pika.BlockingConnection(pika.ConnectionParameters('192.168.1.42'))
channel = connection.channel()
channel.basic_consume(callback,queue='hello',no_ack=True)

print("Start consuming")
channel.start_consuming()