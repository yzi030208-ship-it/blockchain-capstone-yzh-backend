# 发货加分项截图分类索引

共27张ZZL本人原始截图，按证明内容归档。文件名和图像字节保持不变；原附件名、证明范围和SHA256见[截图收据](../screenshot-receipts.json)。正式报告从各组选择清晰且能证明结论的图，不要求全部插入。SH-05/SH-07为同一张限制核验原图，复用时说明即可。

## 合约代码（3张）

- [SH-01a_ZZL_Shipped状态与订单结构](01-code/SH-01a_ZZL_Shipped状态与订单结构.png)
- [SH-01b_ZZL_仅卖家权限检查](01-code/SH-01b_ZZL_仅卖家权限检查.png)
- [SH-01c_ZZL_发货确认与超期退款限制](01-code/SH-01c_ZZL_发货确认与超期退款限制.png)

## 编译与本地测试（1张）

- [SH-02_ZZL_完整编译及19项测试通过](02-tests/SH-02_ZZL_完整编译及19项测试通过.png)

## 账户、部署与Remix连接（7张）

- [SH-03a_ZZL_后端仲裁账户核对](03-deployment/SH-03a_ZZL_后端仲裁账户核对.png)
- [SH-03b_ZZL_部署响应及查询RPC超时](03-deployment/SH-03b_ZZL_部署响应及查询RPC超时.png)
- [SH-03c_ZZL_部署后地址仲裁人与区块查询成功](03-deployment/SH-03c_ZZL_部署后地址仲裁人与区块查询成功.png)
- [SH-03d1_ZZL_Remix发货版本为1](03-deployment/SH-03d1_ZZL_Remix发货版本为1.png)
- [SH-03d2_ZZL_Remix仲裁人查询](03-deployment/SH-03d2_ZZL_Remix仲裁人查询.png)
- [SH-03d3_ZZL_Remix订单数为0](03-deployment/SH-03d3_ZZL_Remix订单数为0.png)
- [SH-03d4_ZZL_Remix买家网络与合约总览](03-deployment/SH-03d4_ZZL_Remix买家网络与合约总览.png)

## 订单创建、授权与付款（6张）

- [SH-04a_ZZL_订单A创建参数及成功记录](04-orders-payment/SH-04a_ZZL_订单A创建参数及成功记录.png)
- [SH-04b-01_ZZL_两枚代币授权确认前](04-orders-payment/SH-04b-01_ZZL_两枚代币授权确认前.png)
- [SH-04c_ZZL_订单0付款后Paid](04-orders-payment/SH-04c_ZZL_订单0付款后Paid.png)
- [SH-04d-01_ZZL_订单B参数及确认前](04-orders-payment/SH-04d-01_ZZL_订单B参数及确认前.png)
- [SH-04e-01_ZZL_订单1付款前Created](04-orders-payment/SH-04e-01_ZZL_订单1付款前Created.png)
- [SH-04e-02_ZZL_订单1付款后Paid](04-orders-payment/SH-04e-02_ZZL_订单1付款后Paid.png)

## 权限与已发货超期退款限制（1张）

真实POTOS eth_call预执行，不签名、不广播，不写成两笔链上失败交易。

- [SH-05_SH-07_ZZL_真实链权限与发货超期退款预执行限制](05-restrictions/SH-05_SH-07_ZZL_真实链权限与发货超期退款预执行限制.png)

## 卖家发货（2张）

- [SH-06a_ZZL_卖家markShipped订单0确认前](06-shipping/SH-06a_ZZL_卖家markShipped订单0确认前.png)
- [SH-06c_ZZL_订单0发货后Shipped](06-shipping/SH-06c_ZZL_订单0发货后Shipped.png)

## 未发货超期退款成功（1张）

- [SH-08-03_ZZL_未发货订单1退款后Refunded](07-refund/SH-08-03_ZZL_未发货订单1退款后Refunded.png)

## 确认收货与卖家收款（1张）

- [SH-09_ZZL_订单0确认收货后Completed](08-completion/SH-09_ZZL_订单0确认收货后Completed.png)

## 最终状态、时间线与余额（1张）

- [SH-10_ZZL_最终两单时间线与余额核验通过](09-final-summary/SH-10_ZZL_最终两单时间线与余额核验通过.png)

## 历史准备资料（1张）

最初候选账户并未用于最终实验，仅作历史准备资料。

- [SH-PREP-01_ZZL_候选卖家账户零POT](prep/SH-PREP-01_ZZL_候选卖家账户零POT.png)

## 实际问题与失败过程（3张）

实际故障记录，不能充当成功结果图。SH-08-01为真实未到期退款失败；SH-08-02是成功后额外请求估算失败；SH-06b为查询超时。

- [SH-06b_ZZL_发货后订单查询超时](issues/SH-06b_ZZL_发货后订单查询超时.png)
- [SH-08-01_ZZL_B未到期退款链上失败](issues/SH-08-01_ZZL_B未到期退款链上失败.png)
- [SH-08-02_ZZL_退款绿色记录与额外请求估算失败](issues/SH-08-02_ZZL_退款绿色记录与额外请求估算失败.png)
