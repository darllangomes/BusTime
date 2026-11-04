# BusTime Recife

Wear OS app para consulta de horários de ônibus da Região Metropolitana do Recife (RMR).

Fork de [BusTime Goiânia](https://github.com/mickaelmendes50/BusTime), adaptado para usar os dados públicos do [Rumo](https://virtual.granderecife.pe.gov.br/rumo/), a plataforma oficial do Grande Recife Consórcio de Transporte.

## ✨ Funcionalidades

- 🚌 Busque qualquer ponto de ônibus da RMR pelo código da parada
- ⏰ Veja os dois próximos horários programados de cada linha que passa no ponto
- 📍 Navegação simples no seu relógio
- ⚡ Rápido e fluido - feito pro Wear OS

## ⌚ App Wear OS Nativo

- 📲 Não depende do celular
- 🤖 Segue o Material Design 3
- 👑 Suporta coroa giratória

## 🛣️ Como usar

1. Toque no ícone de busca
2. Digite o número do ponto (código de parada do Rumo)
3. Veja os próximos horários programados da linha!

## 📱 Telas

<img src="docs/images/home-screen.png" width="200" height="200" alt="home"> <img src="docs/images/search-screen.png" width="200" height="200" alt="search"> <img src="docs/images/buslist-screen.png" width="200" height="200" alt="buslist">

## 📍 Sobre

Dados via endpoints públicos usados pelo próprio site do Rumo (`virtual.granderecife.pe.gov.br/rumo`): paradas, linhas por parada e quadro de horários oficial. Região Metropolitana do Recife - PE.

**Limitação importante:** diferente do app original (que usa previsão de chegada em tempo real da RMTC Goiânia), o Rumo não expõe publicamente uma previsão de chegada ao vivo por parada — o rastreamento de veículos existente no site é vinculado à sessão do navegador (CSRF). Por isso, "PRÓX." e "SEG." aqui mostram os **dois próximos horários programados** da tabela oficial da linha (considerando dia útil, sábado, domingo e exceções de calendário), não uma contagem regressiva em tempo real.
