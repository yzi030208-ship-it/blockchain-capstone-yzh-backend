# 部署后 address 查询超时

原图复用 ../SH-03b_ZZL_部署响应及查询RPC超时.png，不重复复制。部署接口返回了真实地址、交易哈希和区块，随后 address 查询两次出现 RPC timeout / Connect timed out。

独立 RPC 验证回执 0x1、合约代码、仲裁人、版本和部署输入字节后确认部署成功。再次 GET 已恢复 200，响应保存在 ../deployment/backend-address.json。全过程没有重复部署；成功查询的用户原图 SH-03c 已收到并核对，问题已恢复。
