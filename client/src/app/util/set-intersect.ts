export default function setIntersect<T>(setA: Set<T>, setB: Set<T>) {
  const intersectionSet = new Set();

  for (const elem of setA) {
    if (setB.has(elem)) {
      intersectionSet.add(elem);
    }
  }

  return intersectionSet;
}
