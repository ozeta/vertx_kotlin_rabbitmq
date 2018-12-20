import pika
import sys
import Adafruit_DHT as dht
import json
from datetime import datetime, tzinfo, timedelta

def get_time():
    return datetime.now().replace(microsecond=0).isoformat() + "+01:00"

def main(argv):
    pin = 2
    sensor = dht.DHT11
    connection = pika.BlockingConnection(pika.ConnectionParameters(host='192.168.1.42'))
    channel = connection.channel()
    channel.queue_declare(queue='hello')
    while(True):
        humidity, temperature = dht.read_retry(sensor, pin)
        data = dict()
        data["temp (C)"] = temperature
        data["hum (%)"] = humidity
        date = get_time()
        data["date"] = '{}'.format(date)
        print(json.dumps(data))

        channel.basic_publish(exchange='',routing_key='hello',body=json.dumps(data))
    connection.close()
    return

if __name__ == "__main__":
    main(sys.argv[1:])
