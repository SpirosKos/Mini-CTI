  // Helper Functions

  export function getReputation(malicious: number, suspicious: number): string {
    if(malicious > 0) return "malicious";
    if(suspicious > 0 ) return "suspicious";
    return "clean";
  }

  export function calculateScore(malicious: number, suspicious: number, harmless: number): number {
    const total = malicious + suspicious + harmless;

    if(total === 0 ) return 0;

    const malWeight = malicious * 10;
    const susWeight = suspicious * 3;

    return Math.min(100, Math.round((malWeight + susWeight) / total * 10))
  }

  export function getTags(malicious: number, suspicious: number, reputation: number): string[] {
    const tags: string[] = [];
    
    if(reputation > 500) tags.push('High Reputation')
    if(malicious === 0 && suspicious === 0) tags.push("Clean");
    if(malicious > 0) tags.push("Threat Detected");
    if(suspicious > 0 ) tags.push("Suspicious Acitivty");
    
    return tags.length > 0 ? tags : ["Uknown"];

  }

  export function formatDate (isoDate: string): string {
    try {
      
      const date = new Date(isoDate);
      const now = new Date();
      const diffMs = now.getTime() - date.getTime();
      const diffHours = Math.floor(diffMs / (1000 * 60 * 60));

      if(diffHours < 1) return "Less than 1 hour ago";
      if(diffHours < 24 ) return `${diffHours} hours ago`;

      const diffDays = Math.floor(diffHours / 24);

      return `${diffDays} days ago`;

    } catch{
      return 'Uknown';
    }
  }