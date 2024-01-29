module.exports = {
  fakeDocumentCookie: {
    get: () => {
      return this.cookie ? this.cookie : '';
    },
    set: (cookieValue) => {
      if (cookieValue === '') {
        this.cookie = '';
      }
      if (this.cookie) {
        const cookies = this.cookie.split(' ');
        const cookieName = cookieValue.split('=').shift();
        const cookieNameLength = cookieName.length;
        let cookieIndex = -1;
        cookies.forEach((value, index) => {
          if (`${ value.substring(0, cookieNameLength) }=` === `${ cookieName }=`) {
            cookieIndex = index;
          }
        });
        if (cookieIndex > -1) {
          cookies[cookieIndex] = `${ cookieValue };`;
        } else {
          cookies.push(`${ cookieValue };`);
        }
        this.cookie = cookies.join(' ').trim();
      } else {
        this.cookie = cookieValue;
      }
    }
  }
};
