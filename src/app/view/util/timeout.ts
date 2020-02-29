export default function timeout(delay) {
  return new Promise(function(resolve) {
    setTimeout(resolve, delay);
  });
}
