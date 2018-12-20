import pika
import sys
import json
import time
from datetime import datetime, tzinfo, timedelta

def get_time():
    return datetime.now().replace(microsecond=0).isoformat() + "+01:00"

def main(argv):
    print("argv: {}".format(argv))
    time.sleep(2)
    queue_name = argv[0]
    interval = float(argv[1])
    connection = pika.BlockingConnection(pika.ConnectionParameters(host='192.168.1.42'))
    channel = connection.channel()
    channel.queue_declare(queue=queue_name)
    while(True):
        humidity, temperature = (0.0,0.0)
        data = dict()
        data["temp (C)"] = temperature
        data["hum (%)"] = humidity
        date = get_time()
        data["date"] = '{}'.format(date)
        print(json.dumps(data))
        channel.basic_publish(exchange='',routing_key=queue_name,body=json.dumps(data))
        time.sleep(interval)
    connection.close()
    return

if __name__ == "__main__":
    main(sys.argv[1:])
