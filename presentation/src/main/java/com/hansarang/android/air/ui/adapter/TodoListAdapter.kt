package com.hansarang.android.air.ui.adapter

import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hansarang.android.air.databinding.ItemTodoBinding
import com.hansarang.android.air.ui.bind.collapseAnimation
import com.hansarang.android.air.ui.bind.expandAnimation
import com.hansarang.android.air.ui.bind.setExpend
import com.hansarang.android.air.ui.bind.setToggleEnabled
import com.hansarang.android.air.ui.decorator.ItemDividerDecorator
import com.hansarang.android.air.ui.extention.dp
import com.hansarang.android.air.ui.viewmodel.fragment.TodoViewModel
import com.hansarang.android.domain.entity.dto.Todo

class TodoListAdapter(private val viewModel: TodoViewModel): ListAdapter<Todo, TodoListAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(
        private val binding: ItemTodoBinding
    ): RecyclerView.ViewHolder(binding.root) {

        private fun init(todo: Todo) = with(binding) {
            if (todo.isExpended) {
                nestedScrollViewCheckListTodo.setExpend()
                binding.btnExpendTodo.isSelected = true
            }
            ivExpendTodo.setToggleEnabled(todo.isExpended)
        }

        fun bind(todo: Todo, position: Int) = with(binding) {

            init(todo)

            val checkListAdapter = CheckListAdapter()
            rvCheckListTodo.run {
                adapter = checkListAdapter
                if (todo.checkList.isEmpty()) {
                    linearLayoutHorizontalCheckListTodo.setPadding(0,0,0, 10.dp)
                } else {
                    linearLayoutHorizontalCheckListTodo.setPadding(0,0,0,0)
                    checkListAdapter.submitList(todo.checkList)
                }
                if (itemDecorationCount == 0) {
                    addItemDecoration(ItemDividerDecorator(5.dp))
                }
            }

            binding.todo = todo
            btnExpendTodo.setOnClickListener {
                todo.isExpended = !todo.isExpended
                ivExpendTodo.setToggleEnabled(todo.isExpended)
                binding.btnExpendTodo.isSelected = if (todo.isExpended) {
                    nestedScrollViewCheckListTodo.expandAnimation()
                    true
                } else {
                    nestedScrollViewCheckListTodo.collapseAnimation()
                    false
                }
            }

            etAddCheckListTodo.setOnKeyListener { _, keyCode, keyEvent ->
                if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_DOWN) {
                    if (etAddCheckListTodo.text.isNotEmpty()) {
                        viewModel.postCheckList(todo.title, etAddCheckListTodo.text.toString())
                        etAddCheckListTodo.setText("")
                        notifyItemChanged(position)
                    }
                }
                return@setOnKeyListener true
            }

            checkListAdapter.setOnClickDeleteListener { value ->
                viewModel.deleteCheckList(todo.title, value)
                notifyItemChanged(position)
            }

            checkListAdapter.setOnClickModifyListener { beforeValue, afterValue ->
                viewModel.putCheckList(todo.title, beforeValue, afterValue)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTodoBinding.inflate(inflater)
        binding.root.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<Todo>() {
            override fun areItemsTheSame(oldItem: Todo, newItem: Todo): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Todo, newItem: Todo): Boolean {
                return oldItem == newItem
            }
        }
    }
}