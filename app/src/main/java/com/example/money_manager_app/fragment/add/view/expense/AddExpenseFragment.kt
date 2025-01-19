package com.example.money_manager_app.fragment.add.view.expense

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
import com.example.money_manager_app.databinding.FragmentAddExpenseBinding
import com.example.money_manager_app.fragment.add.view.AddFragment
import com.example.money_manager_app.fragment.add.viewmodel.AddViewModel
import com.example.money_manager_app.utils.toDateTimestamp
import com.example.money_manager_app.utils.toTimeTimestamp
import com.example.money_manager_app.viewmodel.MainViewModel
import com.example.moneymanager.ui.add.adapter.AddTransferInterface
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddExpenseFragment : BaseFragment<FragmentAddExpenseBinding, ExpenseViewModel>(R.layout.fragment_add_expense), AddTransferInterface {

    override fun getVM(): ExpenseViewModel {
        val viewModel : ExpenseViewModel by activityViewModels()
        return viewModel
    }

    private val addViewModel: AddViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private var checkEdit = false

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
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
        setAmount()
        selectWallet()
        selectCategory()
        getVM().getDateTime()
    }

    override fun onBack() {
        super.onBack()
        (parentFragment as? AddFragment)?.onBack()
    }

    fun setAmount(){
        val navigation = findNavController()
        binding.etAmount.setOnClickListener{
            val bundle = bundleOf("type" to TransferType.Expense)
            navigation.navigate(R.id.framgmentCaculator, bundle)
        }
    }

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        bitmap?.let {
            getVM().setBitmap(it)
        }
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

    private fun setData(){
        var amount = binding.etAmount.text.toString()
        val description = binding.etDescription.text.toString()
        val momo = binding.etMemo.text.toString()
        if (amount.isEmpty()) {
            amount = "0"
        }
        getVM().setAmount(amount.toDouble())
        getVM().setDescriptor(description)
        getVM().setMomo(momo)
    }

    private fun selectCategory(){
        val navController = findNavController()
        binding.etCategory.setOnClickListener {
            setData()
            val bundle = bundleOf("type" to TransferType.Expense.toString())
            navController.navigate(R.id.selectIncomeExpenseFragment,bundle)
        }
    }

    private fun selectWallet(){
        val navController = findNavController()
        binding.spWallet.setOnClickListener{
            setData()
            val bundle = bundleOf(
                "typeExpense" to 1,
                "isCheckWallet" to true,
            )
            navController.navigate(R.id.selectWalletFragment,bundle)
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
                getVM().categoryNameExpense.collect { category ->
                    binding.etCategory.text =category.first
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
                    if(amount != 0.0){
                        binding.etAmount.text = amount.toString()
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
                        binding.spWallet.text = wallet.first().name
                    }
                }
            }
        }
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
        val amountText = binding.etAmount.text.toString()
        var fromWallet = 0L
        try {
            fromWallet = getVM().fromWallet.value.first().id
        } catch (e: Exception) {
            Log.d("TAG", "onSaveExpense: ${e.message}")
        }
        val iconId : Int = mainViewModel.categories.value.find { it.name == getVM().getCategoryNameExpense().first }?.iconId ?: 0
        val idCategory = mainViewModel.categories.value.find { it.name == getVM().getCategoryNameExpense().first }?.id ?: 0
        if (amountText.isNotEmpty() && fromWallet != 0L && iconId != 0 && idCategory != 0L) {
            val amount = amountText.toDouble()
            val description = binding.etDescription.text.toString()
            val time = binding.etTime.text.toString()
            val date = binding.etDate.text.toString()
            val getbitmap = getVM().getBitmap()
            var linkimg = getVM().saveDrawableToAppStorage( getbitmap)
            if(linkimg == null){
                linkimg = ""
            }
            val memo = binding.etMemo.text.toString()
            val toWallet = fromWallet
            val accountId = mainViewModel.currentAccount.value?.account?.id ?: 0
            val name = binding.etMemo.text.toString()

            val transfer = Transfer(
                0,
                fromWallet,
                toWallet,
                amount,
                name,
                0.0,
                description,
                accountId,
                linkimg,
                date.toDateTimestamp(),
                time.toTimeTimestamp(),
                TransferType.Expense,
                iconId,
                idCategory,
                memo
            )
            getVM().saveIncomeAndExpense(transfer)
            getVM().onCleared()
            addViewModel.onCleared()
            findNavController().popBackStack()
        } else {
            Toast.makeText(requireContext(), context?.getString(R.string.please_reenter), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSaveTransfer() {

    }

    override fun onEdit() {
        val amountText = binding.etAmount.text.toString()
        var fromWallet = 0L
        try {
            fromWallet = getVM().fromWallet.value.first().id
        } catch (e: Exception) {
            Log.d("TAG", "onSaveExpense: ${e.message}")
        }
        val iconId : Int = mainViewModel.categories.value.find { it.name == getVM().getCategoryNameExpense().first }?.iconId ?: 0
        val idCategory = mainViewModel.categories.value.find { it.name == getVM().getCategoryNameExpense().first }?.id ?: 0
        if (amountText.isNotEmpty() && fromWallet != 0L && iconId != 0 && idCategory != 0L) {
            val amount = amountText.toDouble()
            val description = binding.etDescription.text.toString()
            val time = binding.etTime.text.toString()
            val date = binding.etDate.text.toString()
            val getbitmap = getVM().getBitmap()
            var linkimg = getVM().saveDrawableToAppStorage(getbitmap)
            if(linkimg == null){
                linkimg = ""
            }
            val memo = binding.etMemo.text.toString()
            val toWallet = fromWallet
            val accountId = mainViewModel.currentAccount.value?.account?.id ?: 0
            val name = binding.etMemo.text.toString()

            val transfer = Transfer(
                getVM().getId(),
                fromWallet,
                toWallet,
                amount,
                name,
                0.0,
                description,
                accountId,
                linkimg,
                date.toDateTimestamp(),
                time.toTimeTimestamp(),
                TransferType.Expense,
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


    override fun onDestroy() {
        super.onDestroy()
        getVM().setCategoryNameExpense(Pair("",0))
    }
}