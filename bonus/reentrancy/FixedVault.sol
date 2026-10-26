// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;

contract FixedVault {
    mapping(address => uint256) public balances;

    function deposit() external payable {
        balances[msg.sender] += msg.value;
    }

    function withdraw() external {
        uint256 amount = balances[msg.sender];
        require(amount > 0, "Vault: nothing to withdraw");

        // 先清零余额，再执行外部转账
        balances[msg.sender] = 0;

        (bool ok, ) = msg.sender.call{value: amount}("");
        require(ok, "Vault: send failed");
    }

    function vaultBalance() external view returns (uint256) {
        return address(this).balance;
    }
}
