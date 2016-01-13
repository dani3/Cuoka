import sys, signal, os
from PyQt4 import QtCore, QtGui, QtWebKit

class WebPage(QtWebKit.QWebPage):
    def __init__(self):
        QtWebKit.QWebPage.__init__(self)
        self.mainFrame().loadFinished.connect(self.handleLoadFinished)

    def process(self, items):
        self._items = iter(items)
        self.fetchNext()

    def fetchNext(self):
        try:
            self._url, self._path, self._shop, self._section, self._man, self._func = next(self._items)
            self.mainFrame().load(QtCore.QUrl(self._url))
        except StopIteration:
            return False
        return True

    def handleLoadFinished(self):
        self._func(self._url, self._path, self._shop, self._section, self._man, self.mainFrame().toHtml())
        if not self.fetchNext():
            print('# processing complete')
            QtGui.qApp.quit()


def funcA(url, path, shop, section, man, html):
    print('# processing:', shop, section)

    new_path = path + '\\' + shop + '_' + section[:section.index('.')] + '_' + man + '.html'

    print(new_path)
    
    html_file = open(new_path, "w")
    html_file.write("%s" % html.encode("utf-8"))
    html_file.close()

if __name__ == '__main__':

    path = 'C:\\Users\\Dani\\Dropbox\\Cuoka\\scrapers_files'
    items = []
    
    #Recorremos las tiendas
    for folder in os.listdir(path):
        shop = folder[:folder.index('_')]
        online = folder[folder.index('_')+1:]
        
        #Si la tienda esta activa...
        if (online == 'true'):
          path_shop = path + '\\' + folder
          
          #Recorremos los dos generos
          for manAux in os.listdir(path_shop):
              man = manAux
              path_shop_gender = path_shop + '\\' + man
              
              #Recorremos cada seccion para cada genero
              for section in os.listdir(path_shop_gender):
                  
                #Si es archivo .txt, asi ignoramos los htmls que se van creando
                if ".txt" in section: 
                    file = open(path_shop_gender + '\\' + section, 'r')
                  
                    url = file.readline().rstrip()

                    items.extend([(url, path_shop_gender, shop, section, man, funcA)])                          

    signal.signal(signal.SIGINT, signal.SIG_DFL)
    print('Press Ctrl+C to quit\n')
    app = QtGui.QApplication(sys.argv)
    webpage = WebPage()
    webpage.process(items)
    sys.exit(app.exec_())
