package com.example.tttn_electronicsstore_manager_admin_app.request

import com.example.tttn_electronicsstore_manager_admin_app.models.ImportReceipt
import com.example.tttn_electronicsstore_manager_admin_app.models.ImportReceiptDetail

data class AddImportReceiptRequest(
    var importReceiptDTO: ImportReceipt,
    var importReceiptDetailDTOList: List<ImportReceiptDetail>
)