# -*- coding: utf-8 -*-
class Review:
    def __init__(self):
        self.table = "review"
        pass

    def to_list(self, values):
        review_list = list()

        review_list.append(values["locationID"])
        review_list.append(values["traceID"])
        review_list.append(values["content"])
        review_list.append(values["imageURL"])
        review_list.append(values["thumbnailURL"])
        review_list.append(values["likeNum"])
        review_list.append(values["lat"])
        review_list.append(values["lon"])
        review_list.append(values["placeName"])
        review_list.append(values["writeDate"])
        review_list.append(values["userName"])
        review_list.append(values["userImageUrl"])
        review_list.append(values["userToken"])
        review_list.append(values["userId"])

        return review_list

    def to_json(self, values):
        review_json = {"locationID": values.locationID,
                       "traceID": values.traceID,
                       "content": values.content,
                       "imageURL": values.imageURL,
                       "thumbnailURL": values.thumbnailURL,
                       "likeNum": values.likeNum,
                       "lat": values.lat,
                       "lon": values.lon,
                       "placeName": values.placeName,
                       "writeDate": values.writeDate,
                       "userName": values.userName,
                       "userImageUrl": values.userImageUrl,
                       "userToken": values.userToken,
                       "userId": values.userId
                       }

        return review_json
