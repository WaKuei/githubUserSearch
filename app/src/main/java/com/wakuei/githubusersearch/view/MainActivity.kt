package com.wakuei.githubusersearch.view

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wakuei.githubusersearch.R
import com.wakuei.githubusersearch.databinding.ActivityMainBinding
import com.wakuei.githubusersearch.viewmodel.UserViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var mBinding: ActivityMainBinding
    private var mAdapter: UserAdapter? = null
    private val mViewModel by viewModel<UserViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        init()
    }

    private fun init() {
        mBinding.btnSearch.setOnClickListener(this)

        mAdapter = UserAdapter(this)
        mBinding.rvList.adapter = mAdapter
        mBinding.rvList.layoutManager = LinearLayoutManager(this)
        mBinding.rvList.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))

        mViewModel.mList.observe(this, {
            mAdapter?.submitList(it)
        })

        mViewModel.mIsLoading.observe(this, {
            if (it) mBinding.pbLoading.visibility = View.VISIBLE
            else mBinding.pbLoading.visibility = View.GONE
        })

        mViewModel.mErrorMessage.observe(this, {
            if (!TextUtils.isEmpty(it)) Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })

        mBinding.rvList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = mBinding.rvList.layoutManager as LinearLayoutManager
                if (mBinding.pbLoading.visibility == View.GONE) {
                    if (mAdapter!=null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == mAdapter!!.itemCount - 1) {
                        mViewModel.searchMoreData(mBinding.etSearch.text.toString())
                    }
                }
            }
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnSearch -> {
                mViewModel.searchUsers(mBinding.etSearch.text.toString())
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(mBinding.btnSearch.windowToken, 0)
            }
        }
    }
}