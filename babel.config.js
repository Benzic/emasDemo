module.exports = function(api) {
  api.cache(true);
  return {
    presets: ['babel-preset-expo'], // 无需指定版本，已通过--save-exact锁定
  };
};