from database import engine
from celery import Celery
import json

celery_app = Celery('data_celery', broker='amqp://', backend='amqp://')
celery_app.config_from_object('celery_config')


def obj_to_json(rv_obj):
    j = {
         "locationID": rv_obj.locationID,
         "traceID": rv_obj.traceID,
         "content": rv_obj.content,
         "imageURL": rv_obj.imageURL,
         "thumbnailURL": rv_obj.thumbnailURL,
         "likeNum": rv_obj.likeNum,
         "lat": rv_obj.lat,
         "lon": rv_obj.lon,
         "placeName": rv_obj.placeName,
         "writeDate": rv_obj.writeDate,
         "userName": rv_obj.userName,
         "userImageUrl": rv_obj.userImageUrl
         }
    return j


@celery_app.task
def insert_review(r_json):
    conn = engine.connect()

    locationID = r_json['locationID']
    traceID = r_json['traceID']
    content = r_json['content']
    imageURL = r_json['imageURL']
    thumbnailURL = r_json['thumbnailURL']
    likeNum = r_json['likeNum']
    lat = r_json['lat']
    lon = r_json['lon']
    placeName = r_json['placeName']
    writeDate = r_json['writeDate']
    userImageUrl = r_json['userImageUrl']
    userName = r_json['userName']
    print userName
    print userImageUrl
  #  q = ("""INSERT INTO review VALUES("%s","%s","%s","%s","%s","%d","%f","%f","%s","%f","%s","%s")""",(locationID, traceID, content, imageURL, thumbnailURL, likeNum, lat, lon, placeName, writeDate, userName, userImageUrl))

#    q = "INSERT INTO review VALUES (?,?,?,?,?,?,?,?,?,?,?,?);",(locationID, traceID, content, imageURL, thumbnailURL, likeNum, lat, lon, placeName, writeDate, userName, userImageUrl)
    q = "INSERT INTO review VALUES('{}','{}','{}','{}','{}',{},{},{},'{}',{},'{}','{}');".format(locationID,
                                                                                                 traceID,
                                                                                                 content,
                                                                                                 imageURL,
                                                                                                 thumbnailURL,
                                                                                                 likeNum,
                                                                                                 lat,
                                                                                                 lon,
                                                                                                 placeName,
                                                                                                 writeDate,
                                                                                                 userName,
                                                                                                 userImageUrl)
#    q = "select * from review"
#    print q
    conn.execute(q)
    conn.close()
    return 'insert ok'


@celery_app.task
def get_review_p(placeName):
    conn = engine.connect()
    q = "SELECT * FROM review WHERE placeName='{}';".format(placeName)
    data = conn.execute(q)
    json_data = []
    for data_list in data:
        json_data.append(otoj(data_list))
    conn.close()
    json_data = {"data": json_data}
    return json_data


@celery_app.task
def get_review_l(lat, lon):
    conn = engine.connect()
    q = "SELECT * FROM review WHERE lat='{}';".format(lat)
    data = conn.execute(q)
    json_data = []
    for data_list in data:
        json_data.append(otoj(data_list))
    conn.close()
    json_data = {"data": json_data}
    return json_data


@celery_app.task
def delete_review(traceID):
    conn = engine.connect()
    q = "DELETE FROM review WHERE traceID='{}';".format(traceID)
    conn.execute(q)
    conn.close()
    return "delete ok"


@celery_app.task
def update_review(traceID):
    conn = engine.connect()
    s = "SELECT likeNum FROM review WHERE traceID ='{}';".format(traceID)
    last_like = conn.execute(s)
    for data_list in last_like:
        like_data = data_list.likeNum
    new_num = like_data + 1
    ud = "UPDATE review SET likeNum = {} WHERE traceID='{}';".format(new_num, traceID)
    conn.execute(ud)
    q = "SELECT * FROM review WHERE traceID='{}';".format(traceID)
    data = conn.execute(q)
    json_data = list()
    for data_list in data:
        json_data.append(otoj(data_list))
    conn.close()
    json_data = {"data": json_data}
    return json_data

# @celery_app.task
# def editpage_review(id):
#     session = Session()
#
#     return 'edit page open ok'
#
#
# @celery_app.task
# def editcommit_review(id):
#     session = Session()
#
#     return 'edit cmt ok'
