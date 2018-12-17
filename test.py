import pika
import sys
import json
from datetime import datetime, tzinfo, timedelta

def get_time():
    return datetime.now().replace(microsecond=0).isoformat() + "+01:00"

def main(argv):
    print("argv: {}".format(argv))
    pin = 2
    connection = pika.BlockingConnection(pika.ConnectionParameters(host='192.168.1.42'))
    channel = connection.channel()
    channel.queue_declare(queue='iot.test')
    while(True):
        humidity, temperature = (0.0,0.0)
        data = dict()
        data["temp (C)"] = temperature
        data["hum (%)"] = humidity
        date = get_time()
        data["date"] = '{}'.format(date)
        print(json.dumps(data))

        channel.basic_publish(exchange='',routing_key='iot.test',body=json.dumps(data))
    connection.close()
    return

if __name__ == "__main__":
    main(sys.argv[1:])
