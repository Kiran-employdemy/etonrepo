# M-39: Anchor Links

**This module is being used/shared by these AEM-Components:**
  - Product Specifications (PDH) Component
  - Resource List (PDH) Component
  - Resource List (Tags) Component

## Module Usage

#### HTML Example: With `<button>` tag (When used for Javascript Behaviors)
```html
<div class="module-anchor-links">
  <ul class="module-anchor-links__list">
    <li class="module-anchor-links__list-item">
      <button class="module-anchor-links__list-link button--reset">
        <span>Link 1</span>
        <i class="icon icon-chevron-right" aria-hidden="true"></i>
      </button>
    </li>
    <li class="module-anchor-links__list-item">
      <button class="module-anchor-links__list-link button--reset">
        <span>Link 2</span>
        <i class="icon icon-chevron-right" aria-hidden="true"></i>
      </button>
    </li>
    <li class="module-anchor-links__list-item">
      <button class="module-anchor-links__list-link button--reset">
        <span>Link 3</span>
        <i class="icon icon-chevron-right" aria-hidden="true"></i>
      </button>
    </li>
  </ul>
</div>
```


#### HTML Example: With `<a href="...">` tag (When used as Regular Links)
```html
<div class="module-anchor-links">
  <ul class="module-anchor-links__list">
    <li class="module-anchor-links__list-item">
      <a href="#link1" target="_blank" class="module-anchor-links__list-link">
        <span>Link 1</span>
        <i class="icon icon-chevron-right" aria-hidden="true"></i>
      </a>
    </li>
    <li class="module-anchor-links__list-item">
      <a href="#link2" target="_blank" class="module-anchor-links__list-link">
        <span>Link 2</span>
        <i class="icon icon-chevron-right" aria-hidden="true"></i>
      </a>
    </li>
    <li class="module-anchor-links__list-item">
      <a href="#link3" target="_blank" class="module-anchor-links__list-link">
        <span>Link 3</span>
        <i class="icon icon-chevron-right" aria-hidden="true"></i>
      </a>
    </li>
  </ul>
</div>
```
