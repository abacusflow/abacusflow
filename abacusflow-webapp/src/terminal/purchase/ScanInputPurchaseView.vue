<template>
  <div class="barcode-scanner-container">
    <!-- 扫描区域 -->
    <div class="scanner-section">
      <div class="scanner-header">
        <h3>条形码扫描采购</h3>
        <a-space>
          <a-button
            type="primary"
            @click="startScanning"
            :disabled="isScanning"
            :loading="isInitializing"
          >
            {{ isScanning ? "扫描中..." : "开始扫描" }}
          </a-button>
          <a-button @click="stopScanning" :disabled="!isScanning"> 停止扫描 </a-button>
        </a-space>
      </div>

      <!-- 摄像头预览 -->
      <div class="camera-container" v-show="isScanning">
        <video ref="videoRef" autoplay muted playsinline></video>
        <div class="scan-overlay">
          <div class="scan-line"></div>
        </div>
      </div>

      <!-- 扫描状态 -->
      <div class="scan-status" v-if="isScanning">
        <a-alert :message="scanStatusMessage" :type="scanStatusType" show-icon />
      </div>
    </div>

    <!-- 扫描结果表单 -->
    <div class="form-section">
      <a-form :model="formState" ref="formRef" @finish="handleSubmit">
        <a-form-item
          label="供应商"
          name="supplierId"
          :rules="[{ required: true, message: '请选择供应商' }]"
        >
          <a-select
            v-model:value="formState.supplierId"
            show-search
            placeholder="请选择供应商"
            optionFilterProp="label"
          >
            <a-select-option
              v-for="supplier in suppliers"
              :key="supplier.id"
              :value="supplier.id"
              :label="supplier.name"
            >
              {{ supplier.name }}
            </a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item
          label="订单日期"
          name="orderDate"
          :rules="[{ required: true, message: '请选择订单日期' }]"
        >
          <a-date-picker
            v-model:value="formState.orderDate"
            :format="dateFormat"
            style="width: 100%"
          />
        </a-form-item>

        <!-- 扫描的商品列表 -->
        <a-form-item label="扫描商品" required>
          <div class="scanned-items">
            <div
              v-for="(item, index) in scannedItems"
              :key="`${item.productId}-${index}`"
              class="scanned-item"
            >
              <div class="item-info">
                <div class="item-name">{{ getProductName(item.productId) }}</div>
                <div class="item-details">
                  <span class="item-type">{{ getProductType(item.productId) }}</span>
                  <span class="item-quantity">数量: {{ item.quantity }}</span>
                  <span class="item-price">单价: ¥{{ item.unitPrice || 0 }}</span>
                </div>
                <div v-if="item.serialNumber" class="item-serial">
                  序列号: {{ item.serialNumber }}
                </div>
              </div>
              <div class="item-actions">
                <a-input-number
                  v-if="!isAssetProduct(item.productId)"
                  v-model:value="item.quantity"
                  :min="1"
                  size="small"
                  style="width: 80px"
                />
                <a-input-number
                  v-model:value="item.unitPrice"
                  :min="0"
                  :precision="2"
                  placeholder="单价"
                  size="small"
                  style="width: 100px"
                />
                <a-button type="link" danger size="small" @click="removeItem(index)">
                  删除
                </a-button>
              </div>
            </div>

            <div v-if="scannedItems.length === 0" class="empty-state">
              暂无扫描商品，请开始扫描条形码
            </div>
          </div>
        </a-form-item>

        <a-form-item label="备注" name="note">
          <a-textarea v-model:value="formState.note" placeholder="请输入备注" :rows="3" />
        </a-form-item>

        <a-form-item :wrapper-col="{ offset: 8, span: 16 }">
          <a-space>
            <a-button
              type="primary"
              html-type="submit"
              :loading="isSubmitting"
              :disabled="scannedItems.length === 0"
            >
              提交采购订单
            </a-button>
            <a-button @click="resetForm">重置</a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </div>

    <!-- 序列号扫描弹窗 -->
    <a-modal
      v-model:open="serialScanModal.visible"
      title="扫描序列号"
      :closable="false"
      :maskClosable="false"
      width="500px"
    >
      <template #footer>
        <a-button @click="cancelSerialScan">取消</a-button>
        <a-button
          type="primary"
          @click="startSerialScanning"
          :loading="serialScanModal.isScanning"
          :disabled="!serialScanModal.pendingItem"
        >
          {{ serialScanModal.isScanning ? "扫描中..." : "开始扫描序列号" }}
        </a-button>
      </template>

      <div class="serial-scan-content">
        <p>
          检测到资产类产品: <strong>{{ serialScanModal.productName }}</strong>
        </p>
        <p>请扫描该产品的序列号</p>

        <div class="serial-camera-container" v-show="serialScanModal.isScanning">
          <video ref="serialVideoRef" autoplay muted playsinline></video>
          <div class="scan-overlay">
            <div class="scan-line"></div>
          </div>
        </div>

        <div v-if="serialScanModal.scannedSerial" class="serial-result">
          <a-alert
            :message="`扫描到序列号: ${serialScanModal.scannedSerial}`"
            type="success"
            show-icon
          />
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
import { inject, reactive, ref, onMounted, onUnmounted } from "vue";
import { message } from "ant-design-vue";
import { BrowserMultiFormatReader } from "@zxing/library";
import dayjs from "dayjs";
import type {
  CreatePurchaseOrderInput,
  PartnerApi,
  ProductApi,
  TransactionApi,
  SelectableProduct,
  PurchaseOrderItemInput
} from "@/core/openapi";

// 扫描采购订单表单类型
type ScanPurchaseOrderInputForm = {
  visible: boolean;
  isScanning: boolean;
  productName: string;
  productId: number;
  scannedSerial: string;
  pendingItem: Partial<PurchaseOrderItemInput> | null;
};

import { useMutation, useQuery } from "@tanstack/vue-query";

// 接口注入
const transactionApi = inject("transactionApi") as TransactionApi;
const partnerApi = inject("partnerApi") as PartnerApi;
const productApi = inject("productApi") as ProductApi;

// 表单引用
const formRef = ref();
const videoRef = ref<HTMLVideoElement>();
const serialVideoRef = ref<HTMLVideoElement>();

// 条形码扫描器
let codeReader: BrowserMultiFormatReader | null = null;
let serialCodeReader: BrowserMultiFormatReader | null = null;

// 扫描状态
const isScanning = ref(false);
const isInitializing = ref(false);
const scanStatusMessage = ref("");
const scanStatusType = ref<"success" | "info" | "warning" | "error">("info");

// 序列号扫描弹窗
const serialScanModal = reactive<ScanPurchaseOrderInputForm>({
  visible: false,
  isScanning: false,
  productName: "",
  productId: 0,
  scannedSerial: "",
  pendingItem: null
});

// 表单状态
const dateFormat = "YYYY/MM/DD";
const formState = reactive({
  supplierId: undefined,
  orderDate: dayjs(dayjs().format(dateFormat), dateFormat),
  note: ""
});

// 扫描的商品列表
const scannedItems = ref<Array<PurchaseOrderItemInput>>([]);
const isSubmitting = ref(false);

// 数据查询
const { data: suppliers } = useQuery({
  queryKey: ["suppliers"],
  queryFn: () => partnerApi.listSelectableSuppliers()
});

const { data: productOptions } = useQuery({
  queryKey: ["selectableProducts"],
  queryFn: () => productApi.listSelectableProducts()
});

// 创建采购订单
const { mutate: createPurchaseOrder } = useMutation({
  mutationFn: (newPurchaseOrder: CreatePurchaseOrderInput) =>
    transactionApi.addPurchaseOrder({ createPurchaseOrderInput: { ...newPurchaseOrder } }),
  onSuccess: () => {
    message.success("采购订单创建成功");
    resetForm();
  },
  onError: (error) => {
    message.error("创建采购订单失败");
    console.error(error);
  }
});

// 开始扫描
const startScanning = async () => {
  try {
    isInitializing.value = true;
    scanStatusMessage.value = "正在初始化摄像头...";
    scanStatusType.value = "info";

    codeReader = new BrowserMultiFormatReader();

    const stream = await navigator.mediaDevices.getUserMedia({
      video: { facingMode: "environment" }
    });

    if (videoRef.value) {
      videoRef.value.srcObject = stream;
      isScanning.value = true;
      scanStatusMessage.value = "请将条形码对准扫描区域";

      // 开始解码
      codeReader.decodeFromVideoDevice(null, videoRef.value, (result, err) => {
        if (result) {
          handleBarcodeScanned(result.getText());
        }
      });
    }
  } catch (error) {
    message.error("摄像头访问失败，请检查权限");
    console.error(error);
  } finally {
    isInitializing.value = false;
  }
};

// 停止扫描
const stopScanning = () => {
  if (codeReader) {
    codeReader.reset();
    codeReader = null;
  }

  if (videoRef.value?.srcObject) {
    const stream = videoRef.value.srcObject as MediaStream;
    stream.getTracks().forEach((track) => track.stop());
  }

  isScanning.value = false;
  scanStatusMessage.value = "";
};

// 处理条形码扫描结果
const handleBarcodeScanned = (barcode: string) => {
  if (!productOptions.value) return;

  // 通过条形码查找产品
  const product = productOptions.value.find((p) => p.barcode === barcode);

  if (!product) {
    message.warning(`未找到条形码为 ${barcode} 的产品`);
    return;
  }

  // 检查是否为资产类产品
  if (isAssetProduct(product.id)) {
    // 资产类产品需要扫描序列号
    showSerialScanModal(product);
  } else {
    // 普通商品直接添加或累加数量
    addOrUpdateItem(product);
  }
};

// 显示序列号扫描弹窗
const showSerialScanModal = (product: SelectableProduct) => {
  serialScanModal.visible = true;
  serialScanModal.productName = product.name;
  serialScanModal.productId = product.id;
  serialScanModal.scannedSerial = "";
  serialScanModal.pendingItem = {
    productId: product.id,
    quantity: 1,
    unitPrice: undefined,
    serialNumber: ""
  };
};

// 开始扫描序列号
const startSerialScanning = async () => {
  try {
    serialScanModal.isScanning = true;
    serialCodeReader = new BrowserMultiFormatReader();

    const stream = await navigator.mediaDevices.getUserMedia({
      video: { facingMode: "environment" }
    });

    if (serialVideoRef.value) {
      serialVideoRef.value.srcObject = stream;

      serialCodeReader.decodeFromVideoDevice(null, serialVideoRef.value, (result, err) => {
        if (result) {
          handleSerialScanned(result.getText());
        }
      });
    }
  } catch (error) {
    message.error("摄像头访问失败");
    console.error(error);
    serialScanModal.isScanning = false;
  }
};

// 处理序列号扫描结果
const handleSerialScanned = (serialNumber: string) => {
  // 检查序列号唯一性
  const existingItem = scannedItems.value.find((item) => item.serialNumber === serialNumber);
  if (existingItem) {
    message.warning(`序列号 ${serialNumber} 已存在`);
    return;
  }

  // 确保 pendingItem 存在
  if (!serialScanModal.pendingItem) {
    message.error("扫描数据异常，请重新扫描");
    return;
  }

  serialScanModal.scannedSerial = serialNumber;
  serialScanModal.pendingItem.serialNumber = serialNumber;

  // 停止序列号扫描
  stopSerialScanning();

  // 添加到扫描列表
  scannedItems.value.push(
    serialScanModal.pendingItem as PurchaseOrderItemInput & { productId: number }
  );
  message.success(`成功添加资产产品: ${serialScanModal.productName}`);

  // 关闭弹窗
  serialScanModal.visible = false;
};

// 停止序列号扫描
const stopSerialScanning = () => {
  if (serialCodeReader) {
    serialCodeReader.reset();
    serialCodeReader = null;
  }

  if (serialVideoRef.value?.srcObject) {
    const stream = serialVideoRef.value.srcObject as MediaStream;
    stream.getTracks().forEach((track) => track.stop());
  }

  serialScanModal.isScanning = false;
};

// 取消序列号扫描
const cancelSerialScan = () => {
  stopSerialScanning();
  serialScanModal.visible = false;
  serialScanModal.pendingItem = null; // 重置待处理项
};

// 添加或更新商品
const addOrUpdateItem = (product: SelectableProduct) => {
  const existingIndex = scannedItems.value.findIndex((item) => item.productId === product.id);

  if (existingIndex !== -1) {
    // 累加数量
    scannedItems.value[existingIndex].quantity += 1;
    message.success(`${product.name} 数量已增加到 ${scannedItems.value[existingIndex].quantity}`);
  } else {
    // 新增商品
    scannedItems.value.push({
      productId: product.id,
      quantity: 1,
      unitPrice: 0,
      serialNumber: undefined
    });
    message.success(`成功添加商品: ${product.name}`);
  }
};

// 移除商品
const removeItem = (index: number) => {
  scannedItems.value.splice(index, 1);
};

// 工具函数
const getProductName = (productId: number): string => {
  const product = productOptions.value?.find((p) => p.id === productId);
  return product?.name || "未知商品";
};

const getProductType = (productId: number): string => {
  const product = productOptions.value?.find((p) => p.id === productId);
  return product?.type === "asset" ? "资产" : "商品";
};

const isAssetProduct = (productId: number): boolean => {
  const product = productOptions.value?.find((p) => p.id === productId);
  return product?.type === "asset";
};

// 提交表单
const handleSubmit = async () => {
  if (scannedItems.value.length === 0) {
    message.warning("请至少扫描一个商品");
    return;
  }

  // 验证单价
  const invalidItems = scannedItems.value.filter((item) => !item.unitPrice || item.unitPrice <= 0);
  if (invalidItems.length > 0) {
    message.warning("请为所有商品设置单价");
    return;
  }

  try {
    isSubmitting.value = true;

    const orderData: CreatePurchaseOrderInput = {
      supplierId: formState.supplierId!,
      orderDate: formState.orderDate.toDate(),
      note: formState.note,
      orderItems: scannedItems.value.map((item) => ({
        productId: item.productId,
        quantity: item.quantity,
        unitPrice: item.unitPrice!,
        serialNumber: item.serialNumber
      }))
    };

    createPurchaseOrder(orderData);
  } catch (error) {
    message.error("提交失败");
    console.error(error);
  } finally {
    isSubmitting.value = false;
  }
};

// 重置表单
const resetForm = () => {
  formRef.value?.resetFields();
  scannedItems.value = [];
  formState.orderDate = dayjs(dayjs().format(dateFormat), dateFormat);
  formState.note = "";
};

// 生命周期
onMounted(() => {
  // 初始化
});

onUnmounted(() => {
  stopScanning();
  stopSerialScanning();
});
</script>

<style scoped>
.barcode-scanner-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.scanner-section {
  margin-bottom: 30px;
}

.scanner-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.scanner-header h3 {
  margin: 0;
  color: #1890ff;
}

.camera-container {
  position: relative;
  width: 100%;
  max-width: 400px;
  margin: 0 auto;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.camera-container video {
  width: 100%;
  height: 300px;
  object-fit: cover;
}

.scan-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.scan-line {
  width: 80%;
  height: 2px;
  background: linear-gradient(90deg, transparent, #ff4d4f, transparent);
  animation: scan 2s linear infinite;
}

@keyframes scan {
  0% {
    transform: translateY(-100px);
  }
  100% {
    transform: translateY(100px);
  }
}

.scan-status {
  margin-top: 15px;
}

.form-section {
  background: #fafafa;
  padding: 24px;
  border-radius: 8px;
  border: 1px solid #e8e8e8;
}

.scanned-items {
  max-height: 400px;
  overflow-y: auto;
}

.scanned-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  margin-bottom: 8px;
  background: white;
  border-radius: 6px;
  border: 1px solid #e8e8e8;
}

.item-info {
  flex: 1;
}

.item-name {
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.item-details {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: #666;
}

.item-type {
  background: #f0f0f0;
  padding: 2px 6px;
  border-radius: 3px;
}

.item-serial {
  font-size: 12px;
  color: #1890ff;
  margin-top: 4px;
}

.item-actions {
  display: flex;
  gap: 8px;
  align-items: center;
}

.empty-state {
  text-align: center;
  color: #999;
  padding: 40px;
  background: white;
  border-radius: 6px;
  border: 1px dashed #d9d9d9;
}

.serial-scan-content {
  text-align: center;
}

.serial-camera-container {
  position: relative;
  width: 100%;
  max-width: 300px;
  margin: 20px auto;
  border-radius: 8px;
  overflow: hidden;
}

.serial-camera-container video {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.serial-result {
  margin-top: 15px;
}
</style>
