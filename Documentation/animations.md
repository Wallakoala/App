# How to animate

- [Drawables](#Drawables)

## Drawables

```java
final AnimationDrawable drawable = new AnimationDrawable();

drawable.addFrame(getDrawable(R.drawable.rounded_button_fill), 0);
drawable.addFrame(getDrawable(R.drawable.rounded_button_border), 0);
drawable.setOneShot(true);
drawable.setEnterFadeDuration(200);
drawable.setExitFadeDuration(200);

btn.setBackground(drawable);
drawable.start();
```

## Resources

- [Expressing continuity](https://material.io/design/motion/#expressing-continuity)
- [Customization](https://material.io/design/motion/customization.html)
