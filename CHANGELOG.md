## Changes
- Implemented the ability to use the `output`'s crafting remainder item for pouring recipe containers. You may do this by not specifying a `container` if the `output` has a crafting remainder. ([#9](https://github.com/MerchantPug/BrewinAndChewin/issues/9))

## Bugfixes
- Fixed vanilla recipe books being unable to place uncombined stacks of items.
- Fixed Coasters always outputting a strength signal of 15. ([#6](https://github.com/MerchantPug/BrewinAndChewin/issues/6))
- Fixed Kippers being compostable. ([#7](https://github.com/MerchantPug/BrewinAndChewin/issues/7))
- Fixed Fiery Fondue Pot not being tagged as `farmersdelight:heat_source` ([#8](https://github.com/MerchantPug/BrewinAndChewin/issues/8))

## Compatibility
- Added any items containing meat to `origins:meats`, and drinks to `origins:ignore_diet`