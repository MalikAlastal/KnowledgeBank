package com.nameisknowledge.knowledgebank.Adapters;


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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestRvAdapter extends RecyclerView.Adapter<TestRvAdapter.Holder>{
    private List<TestRvMD> myList;
    private GenericListener<TestRvMD> listener;
    private boolean input;


    public TestRvAdapter(String answer,boolean input,GenericListener<TestRvMD> listener) {
        this.myList = cutString(answer.toCharArray());
        this.listener = listener;
        this.input = input;
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
    public void onBindViewHolder(@NonNull Holder holder,int position) {
        holder.bind(myList.get(position),position);
    }

    public void setEmpty(int position,TestRvMD testRvMD){
        myList.set(position,new TestRvMD(' ',testRvMD.getIndex()));
        notifyDataSetChanged();
    }

    public void addChar(TestRvMD chr){
        checkEmpty(new GenericListener<List<Integer>>() {
            @Override
            public void getData(List<Integer> list) {
                myList.set(list.get(0),chr);
            }
        });
        notifyDataSetChanged();
    }

    public void checkEmpty(GenericListener<List<Integer>> indexes){
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
                    if (input){
                        setEmpty(position,new TestRvMD(' ',position));
                    }
                    listener.getData(test);
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