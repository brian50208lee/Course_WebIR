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

def DG(rel):
	return np.array(rel) / (np.log2([2] + range(2, len(rel)+1)))

def DCG(rel):
	return np.cumsum(DG(rel))

def NDCG(rel):
	IDCG = DCG(np.sort(rel)[::-1])
	return DCG(rel) / IDCG

def p():
	predict = model.predict(valid_X)
	rel = test_Y.reshape(-1)[np.argsort(predict.reshape(-1))[::-1]]
	print rel, NDCG(rel)[:10]

if __name__ == '__main__':
	'''
	Y, qid, did, X = load_data('../data/train.txt')
	train = {'Y':Y, 'qid':qid, 'did':did, 'X':X}
	np.savez('train.npz', **train)
	'''

	train = np.load('train.npz')
	X, Y = train['X'], train['Y']
	feature_scale = np.load('feature_scale.npz')
	scale_X, dMin_X, dScale_X = feature_scaling(X, feature_scale['dMin_X'], feature_scale['dScale_X'])
	scale_Y, dMin_Y, dScale_Y = feature_scaling(Y, feature_scale['dMin_Y'], feature_scale['dScale_Y'])

	'''
	feature_scale = {}
	feature_scale['dMin_X'] = dMin_X
	feature_scale['dScale_X'] = dScale_X
	feature_scale['dMin_Y'] = dMin_Y
	feature_scale['dScale_Y'] = dScale_Y
	np.savez('feature_scale.npz', **feature_scale)
	'''

	# train and validation
	train_X, train_Y = scale_X[:-1000], scale_Y[:-1000]
	valid_X, valid_Y = scale_X[-1000:], scale_Y[-1000:]
	test_X, test_Y = X[-1000:], Y[-1000:]


	model = LTR.LTR(input_dim=len(train_X[0]), output_dim=len(train_Y[0]), learn_rate=0.01)
	#model.load('model3.npy')
	model.fit(train_X, train_Y, batch_size=1, epochs=1, valid_X=valid_X, valid_Y=valid_Y)
	p()

	#model.save('model3')

