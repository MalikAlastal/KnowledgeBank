package com.nameisknowledge.knowledgebank.adapters;

import com.nameisknowledge.knowledgebank.modelClasses.ModeMD;
import com.nameisknowledge.knowledgebank.R;
import com.nameisknowledge.knowledgebank.databinding.BannerItemModeBinding;
import com.zhpan.bannerview.BaseBannerAdapter;
import com.zhpan.bannerview.BaseViewHolder;

public class MainBannerAdapter extends BaseBannerAdapter<ModeMD> {

    BannerItemModeBinding binding ;

    @Override
    protected void bindData(BaseViewHolder<ModeMD> holder, ModeMD data, int position, int pageSize) {
        binding = BannerItemModeBinding.bind(holder.itemView);

        binding.cardBackground.setCardBackgroundColor(data.getResMainColor());
        binding.imgMode.setImageResource(data.getResImage());
        binding.tvModeName.setText(data.getResName());
        binding.tvModeName.setTextColor(data.getResMainColor());
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.banner_item_mode;
    }
}
