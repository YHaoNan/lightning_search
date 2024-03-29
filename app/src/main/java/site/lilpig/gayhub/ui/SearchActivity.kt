package site.lilpig.gayhub.ui

import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_search.*
import site.lilpig.gayhub.R
import site.lilpig.gayhub.adapter.BookListAdapter
import site.lilpig.gayhub.app
import site.lilpig.gayhub.bookcrawler.SomeUtils
import site.lilpig.gayhub.bookcrawler.core.Book
import site.lilpig.gayhub.bookcrawler.core.CrawlerEngine
import site.lilpig.gayhub.bookcrawler.core.OnBookFoundedListener
import site.lilpig.gayhub.bookcrawler.core.ResourceSite
import site.lilpig.gayhub.bookcrawler.sites.*
import site.lilpig.gayhub.utils.SomeUtil


class SearchActivity : AppCompatActivity(){
    private var crawlerEngine: CrawlerEngine? = null
    private var adapter: BookListAdapter? = null
    private var inputMethodManager: InputMethodManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        window.decorView.systemUiVisibility =
             View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        as_search_bar.hint = app?.getLastKeyword() ?:"搜索"
        bindEvent()
        handleIntent()
    }

    private fun handleIntent() {
        val dataFromLastUI = intent.getStringExtra("keyword")
        if (dataFromLastUI!=null){
            as_search_bar.setText(dataFromLastUI)
            search()
        }else{
            as_search_bar.requestFocus()
            as_search_bar.findFocus()
            inputMethodManager?.showSoftInput(as_search_bar,InputMethodManager.SHOW_FORCED)
        }
    }

    private fun bindEvent() {
        as_search_result.itemAnimator = DefaultItemAnimator()
        as_search_bar.setOnEditorActionListener { textView, i, keyEvent ->
            search()
            true
        }

        as_clear.setOnClickListener({
            as_search_bar.setText("")
        })
    }
    private fun search(){
        var text = as_search_bar.text.toString()
        if (text.isEmpty()){
            text = as_search_bar.hint.toString()
        }
        if(!text.isNullOrEmpty()) {
            app?.setLastKeyword(text)
            adapter = BookListAdapter(text,this,ArrayList())
            adapter?.onItemLongClickListener = AdapterView.OnItemLongClickListener(){l,v,i,a->
                val string = with(StringBuffer()){
                    var book = adapter?.getBook(i)
                    append(book?.name)
                    append("\n\n"+book?.description)
                    toString()
                }
                val intent = Intent(this@SearchActivity,PureTextActivity().javaClass)
                intent.putExtra("text",string)
                startActivity(intent)
                false
            }
            adapter?.onItemClickListener = AdapterView.OnItemClickListener{l,v,i,a->
                if(adapter != null) {
                    var urls = adapter?.getBook(i)?.downloadUrls!!
                    var descs = adapter?.getBook(i)?.downloadUrlDescriptions!!
                    if (urls.size == 1){
                        SomeUtil.openInBrowser(this,urls[0])
                    }else{
                        var dialog = ListDialog(this)
                        dialog.datas = descs
                        dialog.itemClickListener =
                            AdapterView.OnItemClickListener { adapterView: AdapterView<*>, view1: View, i: Int, l: Long ->
                                dialog.dismiss()
                                SomeUtil.openInBrowser(this,urls[i])
                            }
                        dialog.show()
                    }
                }
            }
            as_search_result.adapter = adapter
            as_search_result.layoutManager = LinearLayoutManager(this)
            crawlerEngine?.close()
            crawlerEngine = CrawlerEngine(text,5000,10, object : OnBookFoundedListener {
                override fun onFound(book: Book?) {
                    if (book!=null){
                        adapter?.addBook(book)
                        as_search_result.smoothScrollToPosition(0)
                    }
                }

                override fun onTaskOver() {
                    as_loading.visibility = View.GONE
                }
            })

            crawlerEngine?.sites = mutableListOf(
                IReadWeek(crawlerEngine!!),
                Epubw(crawlerEngine!!),
                PanSoSo(crawlerEngine!!),
                Hejizhan(crawlerEngine!!),
                Book118(crawlerEngine!!),
                Jiumo(crawlerEngine!!)
            )
            if (app?.isGetBookFromLNTULibrary() ?: false)
                crawlerEngine?.sites?.add(LNTULibrary(crawlerEngine!!))

            crawlerEngine?.start()
            as_loading.visibility = View.VISIBLE
            val view = window.peekDecorView()
            if (view != null) {
                inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
    }
}