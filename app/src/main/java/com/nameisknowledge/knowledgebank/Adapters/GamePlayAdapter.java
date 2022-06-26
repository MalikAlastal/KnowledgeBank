package com.nameisknowledge.knowledgebank.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nameisknowledge.knowledgebank.Listeners.GenericListener;
import com.nameisknowledge.knowledgebank.ModelClasses.InputsMD;
import com.nameisknowledge.knowledgebank.R;
import com.nameisknowledge.knowledgebank.databinding.CustomTestRvItemBinding;

import java.util.ArrayList;
import java.util.List;

public class GamePlayAdapter extends RecyclerView.Adapter<GamePlayAdapter.Holder>{
    private List<InputsMD> answer = new ArrayList<>();
    private final GenericListener<InputsMD> listener;
    private final boolean isInput;
    private Context context ;


    public GamePlayAdapter(String answer, boolean isInput, GenericListener<InputsMD> listener) {
        this.answer = cutString(answer.toCharArray());
        this.listener = listener;
        this.isInput = isInput;
    }

    public GamePlayAdapter(boolean isInput, GenericListener<InputsMD> listener) {
        this.listener = listener;
        this.isInput = isInput;
    }

    public List<InputsMD> getMyList() {
        return answer;
    }

    public String covertToString(List<InputsMD> list) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            stringBuilder.append(list.get(i).getLetter());
        }
        return stringBuilder.toString();
    }

    public String getAnswer() {
        return covertToString(answer);
    }

    public void clearArray(){
        answer.removeAll(answer);
        notifyDataSetChanged();
    }

    public void setAnswer(String mAnswer){
        answer = cutString(mAnswer.toCharArray());
        notifyDataSetChanged();
    }

    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext() ;
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_test_rv_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder,int position) {
        holder.bind(answer.get(position),position);
    }

    public void setEmpty(int position, InputsMD inputsMD){
        answer.set(position,new InputsMD(' ', inputsMD.getIndex()));
        notifyDataSetChanged();
    }

    public void addChar(InputsMD chr){
        checkEmpty(list -> answer.set(list.get(0),chr));
        notifyDataSetChanged();
    }

    public void checkEmpty(GenericListener<List<Integer>> indexes){
        List<Integer> num = new ArrayList<>();
        for (int i=0;i<answer.size();i++){
            if (answer.get(i).getLetter() == ' '){
                num.add(i);
                break;
            }
        }
        indexes.getData(num);
    }

    public void setChar(InputsMD chr){
        answer.set(chr.getIndex(),chr);
        notifyDataSetChanged();
    }

    public List<InputsMD> cutString(char[] chars){
        List<InputsMD> listC = new ArrayList<>();
        for (int i=0;i<chars.length;i++) {
            listC.add(new InputsMD(chars[i],i));
        }
        return listC;
    }

    @Override
    public int getItemCount() {
        return answer.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        private final CustomTestRvItemBinding binding;
        private InputsMD test;
        private int position;
        public Holder(@NonNull View itemView) {
            super(itemView);
            binding = CustomTestRvItemBinding.bind(itemView);
            binding.getRoot().setOnClickListener(view -> {
                if (!isInput){
                    setEmpty(position,new InputsMD(' ',position));
                }
                listener.getData(test);
            });
        }

        private void bind(InputsMD string, int position){
            this.test = string;
            this.position = position;
            binding.textView.setText(String.valueOf(string.getLetter()));

            if (!isInput){
                binding.cardText.setCardBackgroundColor(context.getResources().getColor(R.color.white));
                binding.textView.setTextColor(context.getResources().getColor(R.color.black));
            }
        }

    }
}