# -*-coding:utf-8
from image_celery_module import celery_app


@celery_app.task(name='celery_send_image')
def celery_send_image(filename):
    try:
        image_check = open("./image/" + filename, "rb")
        image_check.close()
        return 'good'
    except IOError:
        try :
            fh = open("./image/" + filename + ".txt", 'r')
            s = fh.read()
            fh.close()
            image = open("./image/" + filename, "wb")
            image.write(s.decode('base64'))
            image.close()
            return 'good'
        except:
            return 'Not exist'



@celery_app.task(name='celery_send_thumbnail')
def celery_send_thumbnail(filename):
    try:
        image_check = open("./thumbnail/" + filename, "rb")
        image_check.close()
    except IOError:
        try :
            fh = open("./thumbnail/" + filename + ".txt", 'r')
            s = fh.read()
            fh.close()
            image = open("./thumbnail/" + filename, "wb")
            image.write(s.decode('base64'))
            image.close()
            return 'good'
        except:
            return 'Not exist'


@celery_app.task(name='celery_store_image')
def celery_store_image(filename, str):
    encd_filename = filename + ".txt"
    b64itos = open("./image/"+encd_filename,'a')
    b64itos.write(str)
    b64itos.close()
    return 'good'



@celery_app.task(name='celery_store_thumbnail')
def celery_store_thumbnail(filename, str):
    encd_filename = filename + ".txt"
    b64itos = open("./thumbnail/"+encd_filename, 'a')
    b64itos.write(str)
    b64itos.close()
    return 'good'
