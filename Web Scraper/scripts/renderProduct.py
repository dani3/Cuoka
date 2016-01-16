import sys  
from PyQt4.QtGui import *  
from PyQt4.QtCore import *  
from PyQt4.QtWebKit import *  

url = 'http://www.massimodutti.com/es/es/men/camisas-casual/camisa-blanca-dibujo-c1403341p6954009.html?colorId=250&categoryNav=1403341'

#Take this class for granted.Just use result of rendering.
class Render(QWebPage):  
  def __init__(self, url):  
    self.app = QApplication(sys.argv)  
    QWebPage.__init__(self)  
    self.loadFinished.connect(self._loadFinished)  
    self.mainFrame().load(QUrl(url))  
    self.app.exec_()  
  
  def _loadFinished(self, result):  
    self.frame = self.mainFrame()  
    self.app.quit()  

r = Render(url)  
result = r.frame.toHtml()

print(result)
