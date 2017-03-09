## Internal Repository of Shields

1. Crash detection shield
2. Not at home shield
3. EnOcean button shield
4. EnOcean contact shield
5. EnOcean emergency button shield
6. EnOcean water detection shield
7. Weather shield
8. Wibutler smoke detection shield

If you want to use a shield code, you need to replace `shieldUuid` variable in the shield code.There you need to give `UUID` of your shield.


###Shield Design

- encapsulate in (function(){...code...})();
- `shield id` must be of type ***number***
- when creating a `shield` in database the `shield uuid` must be ***string***
- when creating a `jscode` in database the `shieldUUID` must be a number


- The internal function constructMessage **must** be called
