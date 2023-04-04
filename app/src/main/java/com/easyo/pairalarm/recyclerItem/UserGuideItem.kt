package com.easyo.pairalarm.recyclerItem

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.easyo.pairalarm.R
import com.easyo.pairalarm.databinding.ItemSettingBinding
import com.easyo.pairalarm.model.RecyclerItemContentsType
import com.easyo.pairalarm.model.UserGuideContents
import com.easyo.pairalarm.ui.dialog.SimpleDialog
import com.easyo.pairalarm.util.itemBackgroundSetter
import com.xwray.groupie.databinding.BindableItem

class UserGuideItem(
    private val context: Context,
    private val recyclerItemContentsType: RecyclerItemContentsType?,
    override val userGuideContents: UserGuideContents
) : BindableItem<ItemSettingBinding>(userGuideContents.hashCode().toLong()), UserGuideFunctions {
    override fun bind(binding: ItemSettingBinding, position: Int) {
        binding.title = userGuideContents.getTitle(context)
        // 레이아웃의 배경 셋팅
        when (recyclerItemContentsType) {
            RecyclerItemContentsType.SINGLE -> {
                binding.isLastItem = binding.settingItemLayout.itemBackgroundSetter(
                    RecyclerItemContentsType.SINGLE,
                    context,
                    true
                )
            }
            RecyclerItemContentsType.FIRST -> {
                binding.isLastItem = binding.settingItemLayout.itemBackgroundSetter(
                    RecyclerItemContentsType.FIRST,
                    context,
                    false
                )
            }
            RecyclerItemContentsType.LAST -> {
                binding.isLastItem = binding.settingItemLayout.itemBackgroundSetter(
                    RecyclerItemContentsType.LAST,
                    context,
                    true
                )
            }
            else -> {

            }
        }
        binding.root.setOnClickListener {
            when (userGuideContents) {
                UserGuideContents.NOTIFICATION_PERMISSION -> {
                    openNotificationPermissionSetting()
                }
                UserGuideContents.BATTERY_OPTIMIZE -> {
                    openBatteryOptimize()
                }
            }
        }

        when (userGuideContents) {
            UserGuideContents.NOTIFICATION_PERMISSION -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                        == PackageManager.PERMISSION_DENIED){
                        binding.settingDetail = context.getString(R.string.not_set)
                    } else {
                        binding.settingDetail = context.getString(R.string.complete)
                    }
                } else {
                    binding.settingDetail = context.getString(R.string.complete)
                }
            }
            UserGuideContents.BATTERY_OPTIMIZE -> {
                val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
                val isWhiteListing = pm.isIgnoringBatteryOptimizations(context.packageName)
                binding.settingDetail =
                    if (isWhiteListing) context.getString(R.string.complete)
                    else context.getString(R.string.not_set)
            }
        }
    }

    override fun getLayout(): Int = R.layout.item_setting

    override fun openNotificationPermissionSetting() {
        SimpleDialog.showSimpleDialog(
            context,
            title = context.getString(R.string.notification_permission),
            message = context.getString(R.string.notification_permission_description),
            positive = {
                Toast.makeText(context, context.getText(R.string.toast_notification_permission), Toast.LENGTH_LONG).show()
                val intent = Intent().apply {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    data = Uri.fromParts("package", context.packageName, null)
                }
                context.startActivity(intent)
            }
        )
    }

    override fun openBatteryOptimize() {
        SimpleDialog.showSimpleDialog(
            context,
            title = context.getString(R.string.battery_optimize),
            message = context.getString(R.string.battery_optimize_description),
            positive = {
                Toast.makeText(context, context.getText(R.string.toast_set_batter_optimize), Toast.LENGTH_LONG).show()
                val intent = Intent().apply {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    data = Uri.fromParts("package", context.packageName, null)
                }
                context.startActivity(intent)
            }
        )
    }
}