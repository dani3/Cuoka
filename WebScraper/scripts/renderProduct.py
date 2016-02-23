import sys  
from PyQt4.QtGui import *  
from PyQt4.QtCore import *  
from PyQt4.QtWebKit import *
from PyQt4 import QtNetwork
from PyQt4 import QtCore

url = sys.argv[1]
path = sys.argv[2]

class Render(QWebPage):  
  def __init__(self, url):  
    self.app = QApplication(sys.argv)  
    QWebPage.__init__(self)  
    self.loadFinished.connect(self._loadFinished)
    self.request = QtNetwork.QNetworkRequest() 
    self.request.setUrl(QtCore.QUrl(url)) 
    self.request.setRawHeader("Accept-Language", QtCore.QByteArray ("es ,*"))
    self.mainFrame().load(self.request)
    self.app.exec_()  
  
  def _loadFinished(self, result):  
    self.frame = self.mainFrame()  
    self.app.quit()  

r = Render(url)  
result = r.frame.toHtml()

html_file = open(path, "w")
html_file.write("%s" % result.encode("utf-8"))
html_file.close()
