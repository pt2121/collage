# Collage

[Data2Viz](https://data2viz.io) port/wrapper for Jetpack Compose. It is a visualization library, same as [Data2Viz](https://github.com/data2viz/data2viz) and [d3](https://github.com/d3/d3).
This is obviously **not** an official Data2Viz product. Use it at your own risk.

Big thanks to the Data2Viz team and folks who build the Compose graphics vector package.

## Samples ported from [Data2Viz sketches](https://play.data2viz.io/sketches/)

![Wind Simulation](https://raw.githubusercontent.com/pt2121/collage/main/assets/wind-small.gif)

![Climate Change](https://raw.githubusercontent.com/pt2121/collage/main/assets/climateChange-small.gif)

![Hexbin](https://raw.githubusercontent.com/pt2121/collage/main/assets/hexbin.png)

![XYChart](https://raw.githubusercontent.com/pt2121/collage/main/assets/XYChart.png)

![avengersChords](https://raw.githubusercontent.com/pt2121/collage/main/assets/avengersChords.png)

## Usage

Snapshots are available in Sonatype's s01 snapshots repository.

```
   repositories {
      google()
      mavenCentral()
      maven { url 'https://s01.oss.sonatype.org/content/repositories/snapshots/' } // for snapshots
   }

   implementation "io.github.pt2121:collage:0.1.0-SNAPSHOT"
```

## License

    Copyright 2022 Prat Tana

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
