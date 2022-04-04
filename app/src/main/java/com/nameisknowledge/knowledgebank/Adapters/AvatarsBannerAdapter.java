package com.nameisknowledge.knowledgebank.Adapters;

import com.nameisknowledge.knowledgebank.ModelClasses.AvatarMD;
import com.nameisknowledge.knowledgebank.R;
import com.nameisknowledge.knowledgebank.databinding.BannerItemAvatarBinding;
import com.zhpan.bannerview.BaseBannerAdapter;
import com.zhpan.bannerview.BaseViewHolder;

public class AvatarsBannerAdapter extends BaseBannerAdapter<AvatarMD> {

    @Override
    protected void bindData(BaseViewHolder<AvatarMD> holder, AvatarMD data, int position, int pageSize) {
        BannerItemAvatarBinding binding = BannerItemAvatarBinding.bind(holder.itemView);

        binding.imgAvatar.setImageResource(data.getAvatarRes());
        binding.tvAvatarTitle.setText(data.getAvatarTitle());
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.banner_item_avatar;
    }
}
