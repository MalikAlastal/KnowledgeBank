package com.nameisknowledge.knowledgebank.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nameisknowledge.knowledgebank.Listeners.GenericListener;
import com.nameisknowledge.knowledgebank.ModelClasses.TestRvMD;
import com.nameisknowledge.knowledgebank.R;
import com.nameisknowledge.knowledgebank.databinding.CustomTestRvItemBinding;

import java.util.ArrayList;
import java.util.List;

public class TestRvAdapter extends RecyclerView.Adapter<TestRvAdapter.Holder>{
    private int type;
    private List<TestRvMD> myList;
    private GenericListener<TestRvMD> listener;


    public TestRvAdapter(String answer, GenericListener<TestRvMD> listener,int type) {
        this.myList = cutString(answer.toCharArray());
        this.type = type;
        this.listener = listener;
    }

    public List<TestRvMD> getMyList() {
        return myList;
    }

    public void clearArray(){
        myList.removeAll(myList);
        notifyDataSetChanged();
    }

    public void setMyList(List<TestRvMD> myList) {
        this.myList = myList;
    }

    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_test_rv_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(myList.get(position),position);
    }

    public void addChar(TestRvMD chr){
        checkEmpty(new GenericListener<List<Integer>>() {
            @Override
            public void getData(List<Integer> integer) {
                if (myList.size() != 0){
                    if (integer.size() == 0){
                        myList.add(chr);
                    }else {
                        myList.set(integer.get(0),chr);
                    }
                }else {
                    myList.add(chr);
                }
            }
        });
        notifyDataSetChanged();
    }

    private void checkEmpty(GenericListener<List<Integer>> indexes){
        List<Integer> num = new ArrayList<>();
        for (int i=0;i<myList.size();i++){
            if (myList.get(i).getLetter() == ' '){
                num.add(i);
                break;
            }
        }
        indexes.getData(num);
    }

    public void setChar(TestRvMD chr){
        myList.set(chr.getIndex(),chr);
        notifyDataSetChanged();
    }

    public List<TestRvMD> cutString(char[] chars){
        List<TestRvMD> listC = new ArrayList<TestRvMD>();
        for (int i=0;i<chars.length;i++) {
            listC.add(new TestRvMD(chars[i],i));
        }
        return listC;
    }


    @Override
    public int getItemCount() {
        return myList.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        private CustomTestRvItemBinding binding;
        private TestRvMD test;
        private int position;
        public Holder(@NonNull View itemView) {
            super(itemView);
            binding = CustomTestRvItemBinding.bind(itemView);
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.getData(test);
                    myList.set(position, new TestRvMD(' ',test.getIndex()));
                    notifyDataSetChanged();
                }
            });
        }
        private void bind(TestRvMD string,int position){
            this.test = string;
            this.position = position;
            binding.textView.setText(String.valueOf(string.getLetter()));
        }

    }
}