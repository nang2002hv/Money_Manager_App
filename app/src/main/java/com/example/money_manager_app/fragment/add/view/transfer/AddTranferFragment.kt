package com.example.money_manager_app.fragment.add.view.transfer

import android.app.AlertDialog
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.money_manager_app.R
import com.example.money_manager_app.base.fragment.BaseFragment
import com.example.money_manager_app.data.model.entity.Transfer
import com.example.money_manager_app.data.model.entity.enums.TransferType
import com.example.money_manager_app.databinding.FragmentAddTranferBinding
import com.example.money_manager_app.fragment.add.view.AddFragment
import com.example.money_manager_app.fragment.add.viewmodel.AddViewModel
import com.example.money_manager_app.utils.toDateTimestamp
import com.example.money_manager_app.utils.toTimeTimestamp
import com.example.money_manager_app.viewmodel.MainViewModel
import com.example.moneymanager.ui.add.adapter.AddTransferInterface
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class AddTranferFragment : BaseFragment<FragmentAddTranferBinding,TransferViewModel>(R.layout.fragment_add_tranfer), AddTransferInterface {

    private var btnFee = false
    private val mainViewModel: MainViewModel by activityViewModels()
    private var checkEdit = false
    private val addViewModel: AddViewModel by activityViewModels()

    override fun getVM(): TransferViewModel {
        val viewModel: TransferViewModel by activityViewModels()
        return viewModel
    }

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        bitmap?.let {
            getVM().setBitmap(it)
        }
    }

    override fun onBack() {
        super.onBack()
        (parentFragment as? AddFragment)?.onBack()
    }

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            try {
                val bitmap = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(requireContext().contentResolver, it))
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(requireContext().contentResolver, it)
                }
                getVM().setBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } ?: run {
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            takePictureLauncher.launch(null)
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        if(addViewModel.getCheckEdit()){
            getVM().getDateTime()
        } else {
            if(!checkEdit){
                getVM().updateDateTime()
                checkEdit = true
            }
        }
        pickDate()
        pickTime()
        selectImage()
        observe()
        selectFromWallet()
        setAmount()
        selectToWallet()
        setButtonFee()
    }

    private fun setAmount(){
        binding.etAmount.setOnClickListener{
            val bundle = bundleOf("type" to TransferType.Transfer)
            findNavController().navigate(R.id.framgmentCaculator,bundle)
        }
    }

    private fun setButtonFee() {
        binding.ivFee.setOnClickListener {
            if(btnFee){
                binding.ivFee.setImageResource(R.drawable.toggle_off)
                binding.etFee.isEnabled = false
                btnFee = false
            } else {
                binding.ivFee.setImageResource(R.drawable.toggle_on)
                binding.etFee.isEnabled = true
                btnFee = true
            }
        }
    }

    private fun selectFromWallet(){
        binding.fromWallet.setOnClickListener{
            setData()
            val bundle = bundleOf(
                "typeExpense" to 2,
                "isCheckWallet" to true,
            )
            findNavController().navigate(R.id.selectWalletFragment,bundle)
        }
    }

    private fun selectToWallet(){
        binding.toWallet.setOnClickListener{
            setData()
            val bundle = bundleOf(
                "typeExpense" to 2,
                "isCheckWallet" to false,
            )
            findNavController().navigate(R.id.selectWalletFragment,bundle)
        }
    }

    fun observe(){
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                getVM().selectedTime.collect { time ->
                    binding.etTime.text = time
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                getVM().selectedDate.collect { date ->
                    binding.etDate.text = date
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                getVM().currentDateTime.collect { dateTime ->
                    binding.etDate.text = dateTime.first
                    binding.etTime.text = dateTime.second
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                getVM().imageUri.collect { bitmap ->
                    binding.ivImage.setImageBitmap(bitmap)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                getVM().amount.collect { amount ->
                    val currentCurrency = mainViewModel.currentAccount.value!!.account.currency
                    val currencySymbol = getString(currentCurrency.symbolRes)
                    if(amount != 0.0){
                        binding.etAmount.text = context?.getString(R.string.money_amount, currencySymbol, amount)
                    } else {
                        binding.etAmount.text = ""
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                getVM().descriptor.collect { descriptor ->
                    binding.etDescription.setText(descriptor)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                getVM().momo.collect { momo ->
                    binding.etMemo.setText(momo)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                getVM().fromWallet.collect { wallet ->
                    if (wallet.isNotEmpty()) {
                        binding.fromWallet.text = wallet.first().name
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                getVM().toWallet.collect { wallet ->
                    if (wallet.isNotEmpty()) {
                        binding.toWallet.text = wallet.first().name
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                getVM().fee.collect { fee ->
                    binding.etFee.setText(fee.toString())
                }
            }
        }


    }


    private fun setData(){
        var amount = binding.etAmount.text.replace("[^\\d.,]".toRegex(), "")
        amount = amount.replace(",", ".")
        val description = binding.etDescription.text.toString()
        val momo = binding.etMemo.text.toString()
        if (amount.isEmpty()) {
            amount = "0"
        }
        getVM().setAmount(amount.toDouble())
        getVM().setDescriptor(description)
        getVM().setMomo(momo)
    }

    private fun selectImage(){
        binding.ivMemo.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setItems(R.array.select_camera_and_gallery) { _, which ->
                when (which) {
                    0 -> {
                        setData()
                        if (requireActivity().checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            takePictureLauncher.launch(null)
                        } else {
                            requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                        }
                    }
                    1 -> {
                        setData()
                        pickImageLauncher.launch("image/*")
                    }
                }
            }
            builder.show()
        }
    }

    private fun pickTime(){
        binding.etTime.setOnClickListener {
            getVM().showTimePickerDialog(requireContext())
        }

    }


    private fun pickDate(){
        binding.etDate.setOnClickListener {
            getVM().showDatePickerDialog(requireContext())
        }
    }


    override fun onSaveIncome() {
        TODO("Not yet implemented")
    }

    override fun onSaveExpense() {
        TODO("Not yet implemented")
    }

    override fun onSaveTransfer() {
        val amountText = binding.etAmount.text.toString()
        var fromWallet = 0L
        var toWallet = 0L
        try {
            fromWallet = getVM().fromWallet.value.first().id
            toWallet = getVM().toWallet.value.first().id
        } catch (e: Exception) {
            Log.d("TAG", "onSaveIncome: ${e.message}")
        }
        if (amountText.isNotEmpty() &&  fromWallet != 0L && toWallet != 0L) {
            var amount = binding.etAmount.text.replace("[^\\d.,]".toRegex(), "").replace(",", ".").toDouble()
            val description = binding.etDescription.text.toString()
            val time = binding.etTime.text.toString()
            val date = binding.etDate.text.toString()
            val getbitmap = getVM().getBitmap()
            var linkimg = getVM().saveDrawableToAppStorage(requireContext(), getbitmap)
            if(linkimg == null){
                linkimg = ""
            }
            val typeOfExpenditure: TransferType = TransferType.Transfer
            val fee : Double = if(btnFee) binding.etFee.text.toString().toDouble() else 0.0
            val accountId = mainViewModel.currentAccount.value?.account?.id ?: 0
            val name = binding.etMemo.text.toString()
            val iconId = R.drawable.transfer
            val memo = binding.etMemo.text.toString()
            val idCategory = 24L
            val transfer = Transfer(
                0,
                fromWallet,
                toWallet,
                amount,
                name,
                fee,
                description,
                accountId,
                linkimg,
                date.toDateTimestamp(),
                time.toTimeTimestamp(),
                typeOfExpenditure,
                iconId,
                idCategory,
                memo
            )
            getVM().saveIncomeAndExpense(transfer)
            getVM().onCleared()
            findNavController().popBackStack()
        } else {
            Toast.makeText(requireContext(), context?.getString(R.string.please_reenter), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onEdit() {
        val amountText = binding.etAmount.text.toString()
        var fromWallet = 0L
        var toWallet = 0L
        try {
            fromWallet = getVM().fromWallet.value.first().id
            toWallet = getVM().toWallet.value.first().id
        } catch (e: Exception) {
            Log.d("TAG", "onSaveIncome: ${e.message}")
        }
        if (amountText.isNotEmpty() && fromWallet != 0L && toWallet != 0L) {
            var amount = binding.etAmount.text.replace("[^\\d.,]".toRegex(), "").replace(",", ".").toDouble()
            val description = binding.etDescription.text.toString()
            val time = binding.etTime.text.toString()
            val date = binding.etDate.text.toString()
            val getbitmap = getVM().getBitmap()
            var linkimg = getVM().saveDrawableToAppStorage(requireContext(), getbitmap)
            if(linkimg == null){
                linkimg = ""
            }
            val typeOfExpenditure: TransferType = TransferType.Transfer
            val fee : Double = if(btnFee) binding.etFee.text.toString().toDouble() else 0.0
            val accountId = mainViewModel.currentAccount.value?.account?.id ?: 0
            val name = binding.etMemo.text.toString()
            val iconId = R.drawable.transfer
            val memo = binding.etMemo.text.toString()
            val idCategory = 24L
            val transfer = Transfer(
                getVM().getId(),
                fromWallet,
                toWallet,
                amount,
                name,
                fee,
                description,
                accountId,
                linkimg,
                date.toDateTimestamp(),
                time.toTimeTimestamp(),
                typeOfExpenditure,
                iconId,
                idCategory,
                memo
            )
            getVM().editIncomeAndExpense(transfer)
            getVM().onCleared()
            addViewModel.onCleared()
            findNavController().popBackStack()
        } else {
            Toast.makeText(requireContext(), context?.getString(R.string.please_reenter), Toast.LENGTH_SHORT).show()
        }
    }

}