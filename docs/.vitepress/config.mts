import { defineConfig } from 'vitepress'

export default defineConfig({
  title: 'Launch0',
  description: 'The Productivity Launcher for Android',

  head: [
    ['link', { rel: 'icon', type: 'image/png', href: '/favicon.png' }],
    ['meta', { name: 'theme-color', content: '#1a1a2e' }],
    ['meta', { property: 'og:type', content: 'website' }],
    ['meta', { property: 'og:title', content: 'Launch0 - The Productivity Launcher for Android' }],
    ['meta', { property: 'og:description', content: 'A fast, open-source Android launcher. Instant app access, powerful gestures, no ads, no data collection.' }],
    ['meta', { property: 'og:url', content: 'https://launch0.app' }],
  ],

  themeConfig: {
    nav: [
      { text: 'Guide', link: '/guide/' },
      { text: 'Features', link: '/guide/features' },
      { text: 'Privacy', link: '/privacy' },
    ],

    sidebar: {
      '/guide/': [
        {
          text: 'Getting Started',
          items: [
            { text: 'What is Launch0?', link: '/guide/' },
            { text: 'Features', link: '/guide/features' },
            { text: 'FAQs', link: '/guide/faq' },
          ]
        },
        {
          text: 'Contributing',
          items: [
            { text: 'Build from Source', link: '/guide/build' },
            { text: 'Contributing', link: '/guide/contributing' },
          ]
        }
      ]
    },

    socialLinks: [
      { icon: 'github', link: 'https://github.com/ketansp/Olauncher' },
    ],

    footer: {
      message: 'A fork of <a href="https://github.com/tanujnotes/Olauncher">Olauncher</a>. Released under the GPLv3 License.',
      copyright: 'Copyright Launch0 contributors',
    },

    search: {
      provider: 'local',
    },
  },
})
