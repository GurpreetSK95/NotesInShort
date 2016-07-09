

import os
import sys
import havenondemand
import requests

ENDPOINTS_PROJECT_DIR = os.path.join(os.path.dirname(__file__),
                                     'havenondemand-python')

from havenondemand.hodclient import *
client = HODClient("36a143cf-7451-4246-b245-0ac8ddd104ab", version="v1")

print 'imports passed'
sentiment_url = 'https://api.havenondemand.com/1/api/sync/analyzesentiment/v1?text='
ocr_url = 'https://api.havenondemand.com/1/api/sync/ocrdocument/v1?file=@sample_image.png'
def requestCompleted(response, error, **kwargs):
  print response

def ocr(path="/home/satwik/Desktop/angelhack/sample_image.png"):
	params = {'file': path}
	#options for mode - document_photo , scene_photo
	response = client.post_request(params, HODApps.OCR_DOCUMENT, async=False)
	if response is None:
		error = client.get_last_error();
		for err in error.errors:
			print ("Error code: %d \nReason: %s \nDetails: %s\n" % (err.error,err.reason, err.detail))
	print "Ocr done"
	return response['text_block'][0]['text']

def sentiment_analysis(text):
	response = client.get_request({'text': text}, HODApps.ANALYZE_SENTIMENT, async=False)
	if response is None:
		error = client.get_last_error();
		for err in error.errors:
			print ("Error code: %d \nReason: %s \nDetails: %s\n" % (err.error,err.reason, err.detail))
	print 'sentiment analysis done!'
	return response

image_text = ocr()

#do some sentiment analysis
text_sentiments = sentiment_analysis(image_text)
overall_sentiment = text_sentiments["aggregate"]["sentiment"]
positive_sentiment_score = float(text_sentiments["aggregate"]["score"])
postive_sentiments = [ w['sentiment'] for w in text_sentiments["positive"]]
negative_sentiments = [ w['sentiment'] for w in text_sentiments["negative"]]



#response = client.get_request({'text': 'I love Haven OnDemand!'}, HODApps.ANALYZE_SENTIMENT, async=False)
#print response
#res = requests.post(sentiment_url+str('I like it very much'),{'apikey':'36a143cf-7451-4246-b245-0ac8ddd104ab'})
#res = requests.post(ocr_url, {'apikey':'36a143cf-7451-4246-b245-0ac8ddd104ab'})
# print res.content
