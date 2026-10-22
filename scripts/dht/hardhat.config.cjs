// Isolated local validation: no dotenv, real wallet or public-network config.
module.exports = { solidity: "0.8.20", networks: { hardhat: { chainId: 31337 } } };
