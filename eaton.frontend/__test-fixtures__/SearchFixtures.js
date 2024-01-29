module.exports = {
  searchBox: () => {
    return `
    <div class="form-group">
        <label for="site-search-box" class="sr-only"></label>
        <button type="submit" class="button--reset eaton-search__submit eaton-search--default__form-submit">
            <span class="sr-only"></span> <i class="icon icon-search" aria-hidden="true"></i>
        </button>
     </div>
   `;
  },
}

