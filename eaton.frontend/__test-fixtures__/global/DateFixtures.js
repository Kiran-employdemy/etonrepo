module.exports = {
  nowPlus400Days: () => {
    let now = new Date();
    let nowAtMidnight = new Date(now.getFullYear(), now.getMonth(), now.getDate(), 0, 0, 0);
    return new Date(nowAtMidnight.setDate(nowAtMidnight.getDate() + 400));
  }
};
