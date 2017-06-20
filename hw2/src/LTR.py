import math, sys, time
import numpy as np

class LTR(object):
    def __init__(self, input_dim, output_dim, learn_rate=1):
        # set info
        self.input_dim = input_dim
        self.output_dim = output_dim
        self.learn_rate = learn_rate

        # set activate function and loss functino
        self.activate_func = self.act_sigmoid
        self.deactivate_func = self.act_desigmoid
        self.loss_func = self.loss_RMSE

        # init weights [w1, ... , wn, wb]
        self.weights = np.random.randn(self.input_dim+1, self.output_dim)

    def save(self, filepath):
        np.save(filepath, self.weights)

    def load(self, filepath):
        self.weights = np.load(filepath)

    def forword(self, x):
        O = self.activate_func(x.dot(self.weights))
        return O

    def fit_pairwise(self, X, Y, batch_size=1, epochs=1, valid_X=None, valid_Y=None):
        # append base
        X = np.hstack ((X, [[1]]*len(X)))
        for epoch in xrange(1,epochs+1): # epoch
            for offset in xrange(0, len(X)-batch_size+1, batch_size):
                bX, bY = X[offset:offset+batch_size], Y[offset:offset+batch_size], 
                for x1,y1,x2,y2 in zip(bX[:-1],bY[:-1],bX[1:],bY[1:]):
                    # y1 > y2
                    x1,y1,x2,y2 = (x1,y1,x2,y2) if y1 > y2 else (x2,y2,x1,y1)
                    o1, o2 = self.forword(x1), self.forword(x2)

                    dw = (1 - self.deactivate_func(o1-o2)) * (self.deactivate_func(o2)*x2 - self.deactivate_func(o1)*x1)
                    dw = dw.reshape((-1,1))
                    self.weights -= self.learn_rate * dw
                self.__fitlog(epoch=epoch, done_offset=offset+batch_size, max_offset=len(X))
            self.__fitlog(linebreak=True)
            if valid_X!=None and valid_Y!=None:
                self.evaluate(valid_X,valid_Y)

    def fit_listwise(self, X, Y, batch_size=4, epochs=1):
        # append base
        X = np.hstack ((X, [[1]]*len(X)))
        for epoch in xrange(1,epochs+1): # epoch
            offset = 0
            while (offset + batch_size) <= len(X): # batch
                bX, bY = X[offset:offset+batch_size], Y[offset:offset+batch_size], 
                pass


    def fit(self, X, Y, batch_size=1, epochs=1, valid_X=None, valid_Y=None):
        # append base
        X = np.hstack ((X, [[1]]*len(X)))
        for epoch in xrange(1,epochs+1): # epoch
            for offset in xrange(0, len(X)-batch_size+1, batch_size):
                bX, bY = X[offset:offset+batch_size], Y[offset:offset+batch_size]
                fX = self.activate_func(bX.dot(self.weights))
                err = bY - fX
                self.weights += self.learn_rate *  bX.T.dot(err) / len(bX)
                self.__fitlog(epoch=epoch, done_offset=offset+batch_size, max_offset=len(X))
            self.__fitlog(linebreak=True)
            if valid_X!=None and valid_Y!=None:
                self.evaluate(valid_X,valid_Y)

    def __fitlog(self, epoch=0, done_offset=0, max_offset=0, linebreak=False):
        if epoch!=0 and done_offset!=0 and max_offset!=0:
            mes = "    Epoch:{epo:<5} Step:{cur:>}/{max:<}\r"
            mes = mes.format(epo=epoch, cur=done_offset, max=max_offset)
            sys.stdout.write(" "*len(mes)*2+"\r")
            sys.stdout.write(mes)
            sys.stdout.flush()
        if linebreak:  
            sys.stdout.write("\n")
            sys.stdout.flush()

    def evaluate(self, valid_X, valid_Y):
        predict = self.predict(valid_X)
        loss = self.loss_func(predict, valid_Y)
        sys.stdout.write("    Loss:{:<.5f}\n".format(loss))


    def predict(self, X):
        mX = np.hstack((X, [[1]]*len (X)))
        return self.forword(mX)


    def act_sigmoid(self, x):
        x = np.clip(x, -100, 100)
        return 1.0/(1.0+np.exp(-x))

    def act_desigmoid(self, y):
        return y*(1-y)

    def loss_MSE(self, O, Y):
        loss = 0.0
        for o, y in zip(O, Y):
            loss += (o-y)**2
        loss /= len(O)
        return loss

    def loss_RMSE(self, O, Y):
        loss = self.loss_MSE(O, Y)
        loss = math.sqrt(loss)
        return loss

def DG(rel):
    return np.array(rel) / (np.log2([2] + range(2, len(rel)+1)))

def DCG(rel):
    return np.cumsum(DG(rel))

def NDCG(rel):
    IDCG = DCG(np.sort(rel)[::-1])
    return DCG(rel) / IDCG

if __name__ == '__main__':
    '''
    model = LTR(input_dim=1, output_dim=1, learn_rate=1)
    for epoch in xrange(1,6):
        for offset in xrange(1000):
            model._fitlog(epoch,offset,1000)
        model._fitlog(linebreak=True)
    '''
    
    X = np.array([[0,0,0,0],[0,0,1,1],[1,1,0,0],[1,1,1,1],[-1,-1,2,2]])
    Y = np.array([[2],[1],[3],[4],[0]])
    mY = Y.copy().astype(float)
    mY -= np.min(Y)
    mY /= np.max(mY)

    '''
    model = LTR(input_dim=len(X[0]), output_dim=len(Y[0]), learn_rate=1)
    model.fit_pairwise(X, mY, batch_size=5, epochs=5000)
    predict = model.predict(X)

    idx = np.argsort(predict.reshape(-1))[::-1]
    rel = Y.reshape(-1)[idx]
    print rel, NDCG(rel)
    '''

    model = LTR(input_dim=len(X[0]), output_dim=len(Y[0]), learn_rate=0.0001)
    model.fit(X, mY, batch_size=1, epochs=5000, valid_X=X, valid_Y=mY)
    predict = model.predict(X)

    idx = np.argsort(predict.reshape(-1))[::-1]
    rel = Y.reshape(-1)[idx]
    print rel, NDCG(rel)
    




