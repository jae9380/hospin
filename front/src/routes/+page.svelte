<script>
  import "../app.css";
</script>
<h1>Welcome to SvelteKit</h1>
<br>

<button class="btn btn-primary">Hello daisyUI</button>
<br>
<!-- Primary 버튼 -->
<button class="btn btn-primary">Primary</button>

<!-- Secondary 버튼 -->
<button class="btn btn-secondary">Secondary</button>

<!-- Accent 버튼 -->
<button class="btn btn-accent">Accent</button>

<!-- Disabled 버튼 -->
<button class="btn btn-primary" disabled>Disabled</button>

<br>

<div class="card w-96 bg-base-100 shadow-xl">
  <figure>
    <img src="https://placekitten.com/400/300" alt="Kitten" />
  </figure>
  <div class="card-body">
    <h2 class="card-title">귀여운 고양이</h2>
    <p>고양이는 모든 것을 더 귀엽게 만든다.</p>
    <div class="card-actions justify-end">
      <button class="btn btn-primary">Adopt</button>
    </div>
  </div>
</div>

<br>
<div class="alert alert-success">
  <span>성공적으로 저장되었습니다!</span>
</div>

<div class="alert alert-error">
  <span>에러가 발생했습니다.</span>
</div>

<br>

<p>Visit <a href="https://svelte.dev/docs/kit">svelte.dev/docs/kit</a> to read the documentation</p>
