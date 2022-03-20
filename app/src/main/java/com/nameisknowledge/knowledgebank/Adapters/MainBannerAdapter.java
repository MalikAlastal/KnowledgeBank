package com.nameisknowledge.knowledgebank.Adapters;

import com.nameisknowledge.knowledgebank.ModelClasses.UserMD;
import com.nameisknowledge.knowledgebank.R;
import com.nameisknowledge.knowledgebank.databinding.CustomItemUserBinding;
import com.zhpan.bannerview.BaseBannerAdapter;
import com.zhpan.bannerview.BaseViewHolder;

public class MainBannerAdapter extends BaseBannerAdapter<UserMD> {

    CustomItemUserBinding binding ;

    @Override
    protected void bindData(BaseViewHolder<UserMD> holder, UserMD data, int position, int pageSize) {
        binding = CustomItemUserBinding.bind(holder.itemView);

        binding.tvEmail.setText(data.getEmail());
        binding.tvUid.setText(data.getUid());
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.custom_item_user;
    }
}
