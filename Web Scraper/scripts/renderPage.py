import sys  
from PyQt4.QtGui import *  
from PyQt4.QtCore import *  
from PyQt4.QtWebKit import *  

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

url = 'http://www.massimodutti.com/es/es/men/abrigos-y-chaquetas-c1574004.html'  
r = Render(url)  
result = r.frame.toHtml()

html_file = open("dutti.html", "w")
html_file.write("%s" % result.encode("utf-8"))
html_file.close()
