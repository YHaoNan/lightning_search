package site.lilpig.gayhub.bookcrawler.sites

import android.util.Log
import org.jsoup.Jsoup
import site.lilpig.gayhub.bookcrawler.SomeUtils
import site.lilpig.gayhub.bookcrawler.core.*

class PanSoSo(context: Context): ResourceSite(context){
    override fun start() = get("http://www.pansoso.com/zh/"+context.getKeyword(), hashMapOf(SomeUtils.CHROME_USER_AGENT))

    override fun callback() = TaskCallback {
        if (it != null){
            val document = Jsoup.parse(it.body()?.string())
            val psss = document.getElementsByClass("pss")
            for (pss in psss){
                val a = pss.getElementsByTag("a").first()
                val name = a.text()
                val downloadUrls = arrayOf("http://pansoso.com"+a.attr("href"))
                val downloadUrlsDes = arrayOf("去盘搜搜")
                val des = pss.getElementsByClass("des").first().text()
                context.addBook(Book(name,"Unknown",des,"https://s2.ax1x.com/2019/10/25/KaLEb8.th.jpg",downloadUrls,downloadUrlsDes,"盘搜搜"))
            }
        }

    }

}