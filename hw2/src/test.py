import LTR
import numpy as np

def load_data(data_path):
	print 'Process: load_data'
	Y, qid, did, X = [], [], [], []
	for line in open(data_path, 'r').readlines()[:]:
		tokens = line.strip().split()
		y, q, d, x =  tokens[0], tokens[1], tokens[2], tokens[3:]
		y = [float(y)]
		q = q.split(':')[1]
		d = d.split(':')[1]
		x = [float(feature.split(':')[1]) for feature in x]
		Y.append(y), qid.append(q), did.append(d), X.append(x)

	Y, qid, did, X = np.array(Y), np.array(qid), np.array(did), np.array(X)
	return (Y, qid, did, X)

def feature_scaling(X, dMin=None, dScale=None):
	print 'Process: feature_scaling'
	sX = X.copy().astype(float)

	if dMin==None and dScale==None:
		dMin = np.min(sX, axis=0)
		dScale = np.max(sX, axis=0) - dMin
		dScale[dScale==0]=1.0

	sX = (sX - dMin) / dScale
	return sX, dMin, dScale

if __name__ == '__main__':
	Y, qid, did, X = load_data('../data/test.txt')
	feature_scale = np.load('feature_scale.npz')
	scale_X, _, _ = feature_scaling(X, feature_scale['dMin_X'], feature_scale['dScale_X'])


	model = LTR.LTR(input_dim=len(scale_X[0]), output_dim=1, learn_rate=0.01)
	model.load('model3.npy')

	del X
	del Y

	#predict = model.predict(scale_X)
	#rel = Y.reshape(-1)[np.argsort(predict.reshape(-1))[::-1]]

	out = open('predict.csv','w')
	out.write('QueryId,DocumentId\n')
	for q in xrange(10001,10532+1,1):
		m_did = did[qid==str(q)]
		m_X = scale_X[qid==str(q)]
		
		if len(m_X)>0:
			predict = model.predict(m_X)
			rel_id = m_did.reshape(-1)[np.argsort(predict.reshape(-1))[::-1]]
			for d in rel_id[:10]:
				out.write("{},{}\n".format(str(q),d))
	out.close()





