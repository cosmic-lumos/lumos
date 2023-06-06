from django.http import HttpResponse
from django.shortcuts import render
from django.views.decorators.csrf import csrf_exempt
import json
import numpy as np
import joblib

kn_model = joblib.load('./knn_model.pkl')
index = ['cw', 'down', 'up', 'left', 'ccw', 'right']

@csrf_exempt
def predict(request):
    if request.method != 'POST':
        return HttpResponse("Not POST")
    body = json.loads(request.body.decode('utf-8'))
    res_list = body["sensorData"]

    sensor_list = np.zeros(shape=(3, 90), dtype=float)
    i = 0
    for res in res_list:
        sensor_list[0][i] = res['xValue']
        sensor_list[1][i] = res['yValue']
        sensor_list[2][i] = res['zValue']
        i = i + 1

    sensor_list = sensor_list.reshape(1, -1)
    result_index = kn_model.predict(sensor_list)
    print({"gesture": result_index[0]})
    return HttpResponse({"gesture": result_index[0]}, content_type="text/plain")
# Create your views here.
