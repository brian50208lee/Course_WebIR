import sys

ans_file = sys.argv[1] if len(sys.argv) > 1 else "ans_train.csv"
test_file = sys.argv[2] if len(sys.argv) > 2 else "ans.csv"

ans = open(ans_file, "r").readlines()[1:]
test = open(test_file, "r").readlines()[1:]

MAP = 0.0
for ans_line, test_line in zip(ans, test):
	query_id = ans_line.strip().split(",")[0]
	ans_set = set(ans_line.strip().split(",")[1].split())
	test_set = test_line.strip().split(",")[1].split()
	
	m_MAP = 0.0
	correct = set()
	match_seq = []
	for i in xrange(len(test_set)):
		article_id = test_set[i]
		if article_id in ans_set:
			correct.add(article_id)
			m_MAP += float(len(correct))/(i+1)
			match_seq.append((i+1))
	m_MAP /= len(ans_set)

	unmatch = ans_set.difference(correct)
	MAP += m_MAP
	print "id:%-6sans_size:%-5dMAP@100:%-.5f\tunMatch:%s" % (query_id,len(ans_set),m_MAP,unmatch)
	#print "match:",match_seq
print "AvgMAP@100:", 	MAP / len(ans)
