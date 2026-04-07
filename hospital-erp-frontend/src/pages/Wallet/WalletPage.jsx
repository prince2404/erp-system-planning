import { useState } from 'react';
import PageHeader from '../../components/PageHeader.jsx';
import QueryTableCard from '../../components/QueryTableCard.jsx';
import SectionCard from '../../components/SectionCard.jsx';

export default function WalletPage() {
  const [entityType, setEntityType] = useState('CENTER');
  const [entityId, setEntityId] = useState('');
  const [walletId, setWalletId] = useState('');

  return (
    <>
      <PageHeader eyebrow="Phase 6" title="Wallet & Transactions" description="Inspect wallet balances and credit/debit transaction movement produced by billing and refunds." />
      <SectionCard title="Wallet Filters">
        <div className="grid gap-3 md:grid-cols-3">
          <select className="clinical-input" value={entityType} onChange={(event) => setEntityType(event.target.value)}>
            {['CENTER', 'BLOCK', 'DISTRICT', 'STATE'].map((option) => <option key={option}>{option}</option>)}
          </select>
          <input className="clinical-input" placeholder="Entity ID" value={entityId} onChange={(event) => setEntityId(event.target.value)} />
          <input className="clinical-input" placeholder="Wallet ID for transactions" value={walletId} onChange={(event) => setWalletId(event.target.value)} />
        </div>
      </SectionCard>
      <div className="mt-6 grid gap-6 xl:grid-cols-2">
        <QueryTableCard title="Wallet Balance" queryKey="wallet" endpoint="/wallet/balance" params={entityId ? { entityType, entityId: Number(entityId) } : { enabled: false }} columns={[
          { key: 'id', label: 'Wallet ID' },
          { key: 'entityType', label: 'Entity Type' },
          { key: 'entityId', label: 'Entity ID' },
          { key: 'balance', label: 'Balance' },
          { key: 'lastUpdated', label: 'Last Updated' }
        ]} />
        <QueryTableCard title="Transactions" queryKey="transactions" endpoint="/wallet/transactions" params={walletId ? { walletId: Number(walletId) } : { enabled: false }} columns={[
          { key: 'id', label: 'ID' },
          { key: 'type', label: 'Type' },
          { key: 'amount', label: 'Amount' },
          { key: 'referenceId', label: 'Reference' },
          { key: 'description', label: 'Description' },
          { key: 'createdAt', label: 'Created' }
        ]} />
      </div>
    </>
  );
}
